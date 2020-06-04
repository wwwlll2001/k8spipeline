pipeline {
    agent { node('master')}
    stages {
        stage('Preparation') {
            steps {
                checkout scm
                echo 'prepare ok'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew clean test'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Publish') {
            steps {
//                 script {
//                     docker.withRegistry('http://ci:5000', 'docker-login') {
//                       docker.build("fundswatcher-api/fundswatcher-api:${BUILD_NUMBER}").push()
//                     }
//                 }
                sh 'publish ok'
            }
        }
    }

//     post {
// //         always {
// //             archive 'build/libs/**/*.jar'
// //             junit keepLongStdio: true, testResults: '**/test-results/**/*.xml'
// //             jacoco exclusionPattern: '**/test/**'
// //         }
// //         success {
// //             slackSend channel: "#fundswatcher", color: "#00FF00", message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
// //         }
// //         failure {
// //             slackSend channel: "#fundswatcher", color: "#FF0000", message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
// //         }
//     }
}