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

                    load "env_vars.groovy"

                    if(params.triggerMode == 'Please Select'){
                        protocol = env.protocol
                        serverName = env.serverName
                        pathName = env.pathName
                        requestType = env.requestType
                        testFile = "${WORKSPACE}/${env.testFile}"
                        if(env.testFile == '')
                            testFile = "${WORKSPACE}/${env.requestType}Test.jmx"
                    }

                    bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${serverName} -JpathName=${pathName} -JprotocolType =${protocol}  -n -t ${testFile} -l TestResult.jtl"
                }
            }
        }
        stage('Email test results') {
            steps {
                script {
                    load "env_vars.groovy"
                    if(params.triggerMode != 'Please Select'){
                        env.email = params.email
                    }
                    emailext to: env.email,
                        subject: 'JMeter Results',
                        body: 'Attached are the JMeter test results.',
                        attachmentsPattern: 'TestResult.jtl'
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
                        case 'Every Commit':
                            echo 'Running the job every commit'
                            properties([
                                pipelineTriggers([[
                                        $class: 'org.jenkinsci.plugins.ghprb.GhprbTrigger',
                                        triggerOnPush: true,
                                        cron: '*/2 * * * *',
                                        useGitHubHooks: true
                                ]])
                            ])
                        case 'Single Trigger':
                            echo 'Running the job only once'
                        case 'Daily':
                            echo 'Running the job daily at 15:40'
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "40 15 * * *"
                                ]])
                            ])
                        default:
                            error('Invalid trigger type selected')
                    }

                    if(params.triggerMode != 'Please Select'){
                        def envVarsContent = """
                            env.protocol='${params.protocol}'
                            env.serverName='${params.serverName}'
                            env.pathName='${params.pathName}'
                            env.requestType='${params.requestType}'
                            env.testFile='${params.testFile}'
                            env.triggerMode='${params.triggerMode}'
                            env.email='${params.email}'
                        """

                        writeFile file: 'env_vars.groovy', text: envVarsContent
                    }
                }
            }
        }
    }
}


def getCronTrigger(triggerMode){
    echo " - ${triggerMode}"
    switch (triggerMode) {
        case 'Every Minute':
            echo 'Running the job every minute'
            return '* * * * *'
        case 'Every Commit':
            return '0 0 31 2 *'
        case 'Single Trigger':
            echo 'Running the job only once'
            return '0 0 31 2 *'
        case 'Daily':
            echo 'Running the job daily at 15:40'
            return '40 15 * * *'
        default:
            error('Invalid trigger type selected')
    }
}

def getSCMTrigger (triggerMode){
    switch (triggerMode) {
        case 'Every Commit':
            echo 'Running the job every commit'
            return '*/2 * * * *'
        default:
            return '0 0 31 2 *'
    }
}