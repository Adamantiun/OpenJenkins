@Library('jenkins-pipeline-email-ext@2.82') _

pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        choice(name: 'triggerMode', choices: ['Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode')
        string(name: 'email', defaultValue: 'adam.g.nog@gmail.com', description: 'Enter the email address to send JMeter results')
    }

    triggers {
        cron(getCronTrigger(params.triggerMode))
        pollSCM(getSCMTrigger(params.triggerMode))
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    if(params.testFile != '')
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.testFile} -l ${WORKSPACE}/TestResult.jtl"
                    else
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.requestType}Test.jmx -l ${WORKSPACE}/TestResult.jtl"
                }
            }
        }
        stage('Email test results') {
            steps {
                script {
                    emailext(
                        to: params.email,
                        subject: 'JMeter Results',
                        body: 'Attached are the JMeter test results.',
                        attachmentsPattern: '${WORKSPACE}/TestResult.jtl'
                    )
                }
            }
        }
    }
}


def getCronTrigger(triggerMode){
    switch (triggerMode) {
        case 'Every Minute':
            echo 'Running the job every minute'
            return '* * * * *'
        case 'Every Commit':
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
            return '*/2 * * * *'
        default:
            return '0 0 31 2 *'
    }
}