pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: 'www.ojogos.com.br', description: 'Enter the name of the server to run the tests on')
        string(name: 'testFile', defaultValue: 'JJintTest.jmx', description: 'Enter the name of the test file')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    sh 'echo "Hello there"'
                    sh "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -n -t C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/JJintTest.jmx -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl"
                }
            }
        }
    }
}
