pipeline {
    agent any
    
    parameters {
        string(name: 'protocol', defaultValue: 'https', description: 'Enter the protocol type of the server to run the tests on')
        string(name: 'serverName', defaultValue: 'catfact.ninja', description: 'Enter the name of the server to run the tests on')
        string(name: 'pathName', defaultValue: '/fact', description: 'Enter the path to run the tests on')
        choice(name: 'requestType', choices: ['Get', 'Post', 'Put', 'Delete'], description: 'Select the request type to be tested')
        string(name: 'testFile', defaultValue: '', description: 'Enter the name of the test file, if empty will use default files based on ResquestType')
        choice(name: 'triggerMode', choices: ['Please Select', 'Single Trigger', 'Daily', 'Every Commit', 'Every Minute'], description: 'Select the trigger mode, if not selected will use last used trigger mode')
        string(name: 'email', defaultValue: 'adam.g.nog@gmail.com', description: 'Enter the email address to send JMeter results')
    }

    stages {
        stage('Run JMeter tests') {
            steps {
                script {
                    protocol = params.protocol
                    serverName = params.serverName
                    pathName = params.pathName
                    requestType = params.requestType
                    testFile = "${WORKSPACE}/${params.testFile}"
                    if(params.testFile == '')
                        testFile = "${WORKSPACE}/${params.requestType}Test.jmx"

                    if(params.triggerMode == 'Please Select'){
                        try{ load "env_vars.groovy" }
                        catch (Exception ex) {
                            echo "ERROR : missing env_vars.groovy - likely didn't select trigger mode in the first ever build"
                            throw ex
                        }
                        protocol = env.protocol
                        serverName = env.serverName
                        pathName = env.pathName
                        requestType = env.requestType
                        testFile = "${WORKSPACE}/${env.testFile}"
                        if(env.testFile == '')
                            testFile = "${WORKSPACE}/${env.requestType}Test.jmx"
                    }

                    jmeterBinDir = "C:/Users/adanogueira/Desktop/JMeter/apache-jmeter-5.5/bin"
                    jmeterVars = "-JserverName=${serverName} -JpathName=${pathName} -JprotocolType =${protocol}"
                    jmeterProperties = "-Jjmeter.save.saveservice.output_format=csv"

                    try {
                        bat "del ${WORKSPACE}/TestResult.csv"
                    } catch (Exception e) {}

                    bat "cd ${jmeterBinDir} && jmeter.bat -n -t ${testFile} -l ${WORKSPACE}/TestResult.csv ${jmeterVars} ${jmeterProperties}"
                }
            }
        }
        stage('Generate and email report') {
            steps {
                script {
                    //performanceReport parsers: [[$class: 'JMeterParser', glob: 'TestResult.csv']], relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5
                    email = params.email
                    if(params.triggerMode == 'Please Select'){
                        load "env_vars.groovy"
                        email = env.email
                    }
                    csvFile = readFile("${WORKSPACE}/TestResult.csv")
                    emailext to: email,
                        subject: 'JMeter Results',
                        body: getEmailBody(csvFile),
                        attachmentsPattern: 'TestResult.csv',
                        mimeType: 'text/html'
                }
            }
        }
        stage('Set up next build'){
            steps{
                script{
                    triggerMode = params.triggerMode
                    if(triggerMode == 'Please Select'){
                        load "env_vars.groovy"
                        triggerMode = env.triggerMode
                    }
                    switch (triggerMode) {
                        case 'Every Minute':
                        echo 'Running the job every minute'
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "* * * * *"
                                ]])
                            ])
                            break
                        case 'Every Commit':
                            echo 'Running the job every commit'
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.SCMTrigger',
                                        scmpoll_spec  : "*/2 * * * *"
                                ]])
                            ])
                            break
                        case 'Single Trigger':
                            echo 'Running the job only once'
                            break
                        case 'Daily':
                            buildHour = '15'
                            buildMinute = '40'
                            echo "Running the job daily at ${buildHour}:${buildMinute}"
                            properties([
                                pipelineTriggers([[
                                        $class: 'hudson.triggers.TimerTrigger',
                                        spec  : "${buildMinute} ${buildHour} * * *"
                                ]])
                            ])
                            break
                        default:
                            error('Invalid trigger type selected')
                            break
                    }

                    if(params.triggerMode != 'Please Select'){
                        writeFile file: 'env_vars.groovy', text: getEnvVarsContent(params)
                    }
                }
            }
        }
    }
}

def getEnvVarsContent(params){
    return """
env.protocol='${params.protocol}'
env.serverName='${params.serverName}'
env.pathName='${params.pathName}'
env.requestType='${params.requestType}'
env.testFile='${params.testFile}'
env.triggerMode='${params.triggerMode}'
env.email='${params.email}'
"""
}

def getEmailBody(csvFile){
    fisrtHalf = "<!DOCTYPE html>
<html>
<head>
  <title>JMeter Test Report</title>
  <style>
    table {
      border-collapse: collapse;
    }

    th, td {
      border: 1px solid black;
      padding: 8px;
    }
  </style>
</head>
<body>
  <table>
    <tr>
      <th>Label</th>
      <th>Sample Count</th>
      <th>Average Response Time (ms)</th>
      <th>Min</th>
      <th>Max</th>
      <th>Error %</th>
    </tr>
    <script>
      var csvData = "
      secondHalf = ";

      var rows = csvData.split('\\n');
      rows.shift(); // Remove header row

      rows.forEach(function(row) {
        var columns = row.split(',');
        var tableRow = document.createElement('tr');

        columns.forEach(function(column) {
          var tableData = document.createElement('td');
          tableData.textContent = column;
          tableRow.appendChild(tableData);
        });

        document.querySelector('table').appendChild(tableRow);
      });
    </script>
  </table>
</body>
</html>"
    return fisrtHalf + csvFile + secondHalf
}