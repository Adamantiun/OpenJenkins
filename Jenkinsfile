pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        choice(name: 'triggerMode', choices: ['Please Select', 'Single Trigger', 'Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode')
        string(name: 'email', defaultValue: 'adam.g.nog@gmail.com', description: 'Enter the email address to send JMeter results')
    }

    environment{
        finalTriggerMode = params.triggerMode;
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    load "env_vars.groovy"
                    if(params.triggerMode == 'Please Select'){
                        params.protocol = env.protocol
                        params.serverName = env.serverName
                        params.pathName = env.pathName
                        params.requestType = env.requestType
                        params.testFile = env.testFile
                        finalTriggerMode = env.triggerMode
                        params.email = env.email
                    }
                    echo "${finalTriggerMode}"
                    if(params.testFile != '')
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.testFile} -l TestResult.jtl"
                    else
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.requestType}Test.jmx -l TestResult.jtl"
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
    }

    triggers {
        cron(getCronTrigger(finalTriggerMode))
        pollSCM(getSCMTrigger(finalTriggerMode))
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