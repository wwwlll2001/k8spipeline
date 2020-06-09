pipeline {
  environment {
    PROJECT='k8spipeline'
    NAMESPACE='devops'
    ENV='dev'
  }
  agent {
    kubernetes-dev {
      yaml """
apiVersion: v1
kind: Pod
metadata:
 label: worker-${random.uuid}
spec:
 securityContext:
   runAsUser: 0
 containers:
 - name: gradle
   image: gradle:6.3.0
   command:
   - cat
   tty: true
   mountPath:
   - name: volumegradle
     path: /tmp/jenkins/.gradle
 - name: docker
   image: docker
   command:
   - cat
   tty: true
   mountPath:
   - name: volumedocker
     path: /var/run/docker.sock
 - name: kubectl
   image: lachlanevenson/k8s-kubectl:v1.16.10
   command:
   - cat
   tty: true
 - name: helm
   image: lachlanevenson/k8s-helm:v3.1.2
   command:
   - cat
   tty: true
 volumes:
 - name: volumegradle
   hostPath:
     path: /tmp/jenkins/.gradle
 - name: volumedocker
   hostPath:
     path: /var/run/docker.sock
"""
   }
 }
 stages {
   stage('Test') {
     steps {
       container('gradle') {
         script {
           def myRepo=checkout scm
           def gitCommit=myRepo.GIT_COMMIT
           def gitBranch=myRepo.GIT_BRANCH
           try {
             sh """
               pwd
               echo "GIT_BRANCH=${gitBranch}" >> /etc/environment
               echo "GIT_COMMIT=${gitCommit}" >> /etc/environment
               gradle test
               """
           } catch (Exception e) {
             echo 'Failed to test - ${currentBuild.fullDisplayName}'
             echo err.getMessage()
           }
         }
       }
     }
   }
   stage('Build') {
     steps {
       container('gradle') {
         sh "gradle build"
       }
      }
     }
     stage('publish') {
       steps {
         container('docker') {
           script {
             def myRepo = checkout scm
             def gitCommit = myRepo.GIT_COMMIT
             def imageTag = "${env.BUILD_ID}_${gitCommit}"
             docker.withRegistry('https://752535683739.dkr.ecr.cn-northwest-1.amazonaws.com.cn/dev-repository',
             'ecr:cn-northwest-1:95189c1e-6db8-4c81-8e93-3e303e665433') {
               def newApp = docker.build("k8spipeline:${imageTag}")
               newApp.push() // record this snapshot (optional)
               newApp.push 'latest'
             }
           }
         }
       }
     }
     stage('deploy') {
       steps {
         container('helm') {
           script {
             def myRepo = checkout scm
             def gitCommit = myRepo.GIT_COMMIT
             def imageTag = ${env.BUILD_ID}_${gitCommit}
             sh "helm upgrade --install ${env.PROJECT} ./${env.PROJECT} --set image.tag=${imageTag} -f ${env.PROJECT}/values-${env.ENV}.yaml --namespace ${env.NAMESPACE}"
           }
         }
       }
     }
   }
 }
