pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        choice(name: 'triggerMode', choices: ['Please Select', 'Single Trigger', 'Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode, if not selected will use last used trigger mode')
        string(name: 'email', defaultValue: 'adam.g.nog@gmail.com', description: 'Enter the email address to send JMeter results')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    protocol = params.protocol
                    serverName = params.serverName
                    pathName = params.pathName
                    requestType = params.requestType
                    testFile = "${WORKSPACE}/${params.testFile}"
                    if(params.testFile == '')
                        testFile = "${WORKSPACE}/${params.requestType}Test.jmx"

                    if(params.triggerMode == 'Please Select'){
                        try{ load "env_vars.groovy" }
                        catch (Exception ex) {
                            echo "ERROR : missing env_vars.groovy - likely didn't select trigger mode in the first ever build"
                            throw ex
                        }
                        protocol = env.protocol
                        serverName = env.serverName
                        pathName = env.pathName
                        requestType = env.requestType
                        testFile = "${WORKSPACE}/${env.testFile}"
                        if(env.testFile == '')
                            testFile = "${WORKSPACE}/${env.requestType}Test.jmx"
                    }

                    jmeterBinDir = "C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin"
                    jmeterVars = "-JserverName=${serverName} -JpathName=${pathName} -JprotocolType =${protocol}"
                    jmeterProperties = "-Jjmeter.save.saveservice.output_format=xml"

                    try {
                        bat "del ${WORKSPACE}/TestResult.xml"
                    } catch (Exception e) {}

                    bat "cd ${jmeterBinDir} && jmeter.bat -n -t ${testFile} -l ${WORKSPACE}/TestResult.xml ${jmeterVars} ${jmeterProperties}"
                }
            }
        }
        stage('Generate and email report') {
            steps {
                script {
                    performanceReport parsers: [[$class: 'JMeterParser', glob: 'TestResult.xml']], relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5
                    email = params.email
                    if(params.triggerMode == 'Please Select'){
                        load "env_vars.groovy"
                        email = env.email
                    }
                    emailext to: email,
                        subject: 'JMeter Results',
                        body: readFile("${WORKSPACE}/htmlResults/index.html"),
                        attachmentsPattern: 'TestResult.xml',
                        mimeType: 'text/html'
                }
            }
        }
        stage('Set up next build'){
            steps{
                script{
                    triggerMode = params.triggerMode
                    if(triggerMode == 'Please Select'){
                        load "env_vars.groovy"
                        triggerMode = env.triggerMode
                    }
                    switch (triggerMode) {
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
                            buildHour = '15'
                            buildMinute = '40'
                            echo "Running the job daily at ${buildHour}:${buildMinute}"
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "${buildMinute} ${buildHour} * * *"
                                ]])
                            ])
                            break
                        default:
                            error('Invalid trigger type selected')
                            break
                    }

                    if(params.triggerMode != 'Please Select'){
                        writeFile file: 'env_vars.groovy', text: getEnvVarsContent(params)
                    }
                }
            }
        }
    }
}

def getEnvVarsContent(params){
    return """
env.protocol='${params.protocol}'
env.serverName='${params.serverName}'
env.pathName='${params.pathName}'
env.requestType='${params.requestType}'
env.testFile='${params.testFile}'
env.triggerMode='${params.triggerMode}'
env.email='${params.email}'
"""
}