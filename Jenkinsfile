pipeline {
    agent any
    
    parameters {
        string(name: 'serverName', defaultValue: '', description: 'Enter the name of the server to run the tests on')
        choice(name: 'buildSchedule', choices: ['On every commit', 'Once a day'], description: 'Choose when to run the build')
    }

    stages {
        stage('Run JMeter tests') {
            when {
                expression { 
                    if (params.buildSchedule == 'On every commit') {
                        return true // run on every commit
                    } else {
                        // run once a day at 12:00 AM
                        def now = new Date()
                        def today = new Date(now.getYear(), now.getMonth(), now.getDate())
                        def midnight = today.getTime() + (1000 * 60 * 60 * 24)
                        def diff = midnight - now.getTime()
                        return (diff < (1000 * 60 * 5)) // run within the first 5 minutes of the day
                    }
                }
            }
            steps {
                script {
                    def jmeterCommand = "cd C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin && jmeter.bat -n -t C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/JJintTest.jmx -l C:/Users/adanogueira/Desktop/JMeter/tutorial-tests/TestResult1.jtl -Jserver=${params.serverName}"
                    // execute the JMeter tests
                }
            }
        }
    }
}
