pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: 'www.ojogos.com.br', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/jogo/sky-block', description: 'Enter the path to run the tests on')
        string(name: 'testFile', defaultValue: 'JJintTest.jmx', description: 'Enter the name of the test file')
        choice(name: 'triggerMode', choices: ['Daily', 'Every Commit'], description: 'Select the trigger mode')
    }

    triggers {
        cron(enabled: params.triggerMode == 'Daily', expression: '40 12 * * *')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -JserverName=${params.serverName} -JpathName=${params.pathName} -n -t ${WORKSPACE}/${params.testFile} -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                }
            }
        }
    }
}
