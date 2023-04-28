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
                }
            }
        }
    }
}
