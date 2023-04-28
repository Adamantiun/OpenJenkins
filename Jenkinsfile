pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: '', description: 'Enter the name of the server to run the tests on')
        choice(name: 'triggerMode', choices: ['Daily', 'Changes'], description: 'Select the trigger mode')
        booleanParam(name: 'runDaily', defaultValue: false, description: 'Run the pipeline once a day')
        booleanParam(name: 'runOnChanges', defaultValue: false, description: 'Run the pipeline on changes to JMeter tests')
    }

    triggers {
        cron(spec: runDaily ? '0 0 * * *' : '')
        pollSCM(scmpoll_spec: params.triggerMode == 'Changes' ? 'H/5 * * * *' : '')

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
