pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: '', description: 'Enter the name of the server to run the tests on')
        choice(name: 'triggerMode', choices: ['Daily', 'Changes'], description: 'Select the trigger mode')
    }

    triggers {
        cron(enabled: params.triggerMode == 'Daily', expression: '0 0 * * *')
        pollSCM(enabled: params.triggerMode == 'Changes', cron: 'H/5 * * * *')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                bat "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin"
                bat "jmeter.bat -n -t C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/JJintTest.jmx -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl -Jserver=${params.serverName}"
            }
        }
    }
}


