pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        choice(name: 'triggerMode', choices: ['Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode')
    }

    triggers {
        getTrigger(params.triggerMode)
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    if(params.testFile != '')
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.testFile} -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                    else
                        bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.requestType}Test.jmx -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                }
            }
        }
    }
}


def getTrigger(triggerMode){
    switch (triggerMode) {
        case 'Every Minute':
            echo 'Running the job every minute'
            return cron('* * * * *')
        case 'Every Commit':
            echo 'Running the job on every commit'
            return pollSCM('*/2 * * * *')
        case 'Daily':
            echo 'Running the job daily at 11:00'
            return cron('40 15 * * *')
        default:
            error('Invalid trigger type selected')
    }
}