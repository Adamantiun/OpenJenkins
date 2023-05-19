pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: 'JJintTest.jmx', description: 'Enter the name of the test file')
        choice(name: 'triggerMode', choices: ['Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode')
    }

    triggers { cron('* * * * *') }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    if (params.triggerMode == 'Every Commit') {
                        pipelineTriggers([pollSCM('*/2 * * * *')])
                    }
                    bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -p ${WORKSPACE}/${params.requestType}.properties -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.testFile} -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                }
            }
        }
    }
}
