pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'www.ojogos.com.br', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/jogo/sky-block', description: 'Enter the path to run the tests on')
        string(name: 'testFile', defaultValue: 'JJintTest.jmx', description: 'Enter the name of the test file')
        choice(name: 'triggerMode', choices: ['Daily', 'Every Commit'], description: 'Select the trigger mode')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    if (params.triggerMode == 'Every Commit') {
                        pipelineTriggers([pollSCM('*/2 * * * *')])
                    } 
                    if (params.triggerMode == 'Daily') {
                        pipelineTriggers([cron('* 15 * * *')])
                    }
                    bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -JprotocolType =${params.protocol}  -n -t ${WORKSPACE}/${params.testFile} -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                }
            }
        }
    }
}
