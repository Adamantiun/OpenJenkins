pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: '', description: 'Enter the name of the server to run the tests on')
        string(name: 'testFile', defaultValue: 'JJintTest.jmx', description: 'Enter the name of the test file')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    echo ${WORKSPACE}
                    def jmeterCommand = "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -n -t C:/Users/adanogueira/Desktop/Jenkins/OpenJenkins/${params.testFile} -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl -Jserver=${params.serverName}"
                    sh jmeterCommand
                }
            }
        }
    }
}
