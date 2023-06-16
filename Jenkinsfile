pipeline {
    agent any
    
    parameters {
        string(name: 'jmeterBinPath', defaultValue: 'C:/path/to/apache-jmeter-5.5/bin', description: 'Enter the path to the bin folder of the JMeter installation')
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'my.server', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/endPoint', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        string(name: 'bodyData', defaultValue: '', description: 'Enter body data for the test (ie. JSON), will be ignored it requestType is Get')
        choice(name: 'triggerMode', choices: ['Please Select', 'Single Trigger', 'Daily', 'Every Commit', 'Every Minute', 'Cron Expression'], description: 'Select the trigger mode, if not selected will use last used trigger mode')
        string(name: 'cronExpression', defaultValue: '00 12 * * *', description: 'Enter cron expression for more specific trigger timming, will be ignored if triggerMode isn\'t Cron Expression')
        string(name: 'email', defaultValue: 'your.email@company.com', description: 'Enter the email address to send JMeter results')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    protocol = params.protocol
                    serverName = params.serverName
                    pathName = params.pathName
                    requestType = params.requestType
                    bodyData = params.bodyData
                    testFile = "${WORKSPACE}/${params.testFile}"
                    jmeterBinPath = params.jmeterBinPath

                    if(params.testFile == '') // if not specified uses default files based on requestType
                        testFile = "${WORKSPACE}/${params.requestType}Test.jmx"

                    if(params.triggerMode == 'Please Select'){  // get parameters from env_vars.groovy in case of an automatically-triggered build
                        try{ load "env_vars.groovy" }
                        catch (Exception ex) {
                            echo "ERROR : missing env_vars.groovy - likely didn't select trigger mode in the first ever build"
                            throw ex
                        }
                        jmeterBinPath = env.jmeterBinPath
                        protocol = env.protocol
                        serverName = env.serverName
                        pathName = env.pathName
                        requestType = env.requestType
                        bodyData = env.bodyData
                        testFile = "${WORKSPACE}/${env.testFile}"
                        if(env.testFile == '') // if not specified uses default files based on requestType
                            testFile = "${WORKSPACE}/${env.requestType}Test.jmx"
                    }

                    jmeterVars = "-JserverName=${serverName} -JpathName=${pathName} -JprotocolType =${protocol}"

                    if(requestType != 'Get') // adds body data when needed
                        jmeterVars = jmeterVars + " -JbodyData=${bodyData}"
                    
                    jmeterProperties = "-Jjmeter.save.saveservice.output_format=csv"

                    try {
                        bat "del /q ${WORKSPACE}/TestResult.csv" // deletes old file to avoid data accumulation
                    } catch (Exception e) {}
                    
                    // goes to JMeter Bin dir and executes test file
                    bat "cd ${jmeterBinPath} && jmeter.bat -n -t ${testFile} -l ${WORKSPACE}/TestResult.csv ${jmeterVars} ${jmeterProperties}"
                }
            }
        }
        stage('Generate and email report') {
            steps {
                script {
                    // generates tables and graphs for Jenkins to display
                    performanceReport parsers: [[$class: 'JMeterParser', glob: 'TestResult.csv']], relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5

                    email = params.email

                    if(params.triggerMode == 'Please Select'){ // get email from env_vars.groovy in case of an automatically triggered build
                        load "env_vars.groovy"
                        email = env.email
                    }
                    
                    // call custom java CVS to HTML converter for html email 
                    bat "java CSVtoHTMLConverter.java ${WORKSPACE}/TestResult.csv"
                    
                    // sends email
                    emailext to: email,
                        subject: 'JMeter Results',
                        body: readFile("${WORKSPACE}\\report.html"),
                        attachmentsPattern: 'TestResult.csv',
                        mimeType: 'text/html'
                }
            }
        }
        stage('Set up next build'){
            steps{
                script{
                    triggerMode = params.triggerMode
                    cronExpression = params.cronExpression

                    if(triggerMode == 'Please Select'){ // get parameters from env_vars.groovy in case of an automatically triggered build
                        load "env_vars.groovy"
                        triggerMode = env.triggerMode
                        cronExpression = env.cronExpression
                    }

                    switch (triggerMode) { // creates pipelineTriggers based on user choice
                        case 'Every Minute':
                        echo 'Running the job every minute'
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "* * * * *"
                                ]])
                            ])
                            break
                        case 'Every Commit':
                            echo 'Running the job every commit'
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.SCMTrigger',
                                        scmpoll_spec  : "*/2 * * * *"
                                ]])
                            ])
                            break
                        case 'Single Trigger':
                            echo 'Running the job only once'
                            break
                        case 'Daily':
                            buildHour = '00'
                            buildMinute = '00'
                            echo "Running the job daily at ${buildHour}:${buildMinute}"
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "${buildMinute} ${buildHour} * * *"
                                ]])
                            ])
                            break
                        case 'Cron Expression':
                            echo "Running the job with cron expression ${cronExpression}"
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "${cronExpression}"
                                ]])
                            ])
                            break
                        default:
                            error('Invalid trigger type selected')
                            break
                    }

                    if(params.triggerMode != 'Please Select'){ // store parameters if build wasn't automatically triggered
                        writeFile file: 'env_vars.groovy', text: getEnvVarsContent(params)
                    }
                }
            }
        }
    }
}

def getEnvVarsContent(params){
    // convert parameters to string for storing
    return """
env.jmeterBinPath='${params.jmeterBinPath}'
env.protocol='${params.protocol}'
env.serverName='${params.serverName}'
env.pathName='${params.pathName}'
env.requestType='${params.requestType}'
env.testFile='${params.testFile}'
env.bodyData='${params.bodyData}'
env.triggerMode='${params.triggerMode}'
env.cronExpression='${params.cronExpression}'
env.email='${params.email}'
"""
}
