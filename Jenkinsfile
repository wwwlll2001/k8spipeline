def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(
    label: label, runAsUser: "0", cloud: 'kubernetes-dev',
    containers: [
      containerTemplate(name: 'gradle', image: 'gradle:6.3.0', command: 'cat', ttyEnabled: true),
      containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
      containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.16.10', command: 'cat', ttyEnabled: true),
      containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:v3.1.2', command: 'cat', ttyEnabled: true)
    ],
    volumes: [
      hostPathVolume(mountPath: '/home/gradle/.gradle', hostPath: '/tmp/jenkins/.gradle'),
      hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
    ]
)
{
  node(label) {
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT
    def gitBranch = myRepo.GIT_BRANCH
    def shortGitCommit = "${gitCommit[0..10]}"
    def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)

    stage('Test') {
      try {
        container('gradle') {
          sh """
            pwd
            echo "GIT_BRANCH=${gitBranch}" >> /etc/environment
            echo "GIT_COMMIT=${gitCommit}" >> /etc/environment
            gradle test
            """
        }
      }
      catch (exc) {
        println "Failed to test - ${currentBuild.fullDisplayName}"
        throw(exc)
      }
    }
    stage('Build') {
      container('gradle') {
        sh "gradle build"
      }
    }
    stage('publish') {
      container('docker') {
        docker.withRegistry('https://752535683739.dkr.ecr.cn-northwest-1.amazonaws.com.cn/dev-repository',
                            'ecr:cn-northwest-1:95189c1e-6db8-4c81-8e93-3e303e665433') {
//           docker.build("k8spipeline:${env.BUILD_ID}_${gitCommit}").push().push("latest")
          def newApp = docker.build("k8spipeline:${env.BUILD_ID}_${gitCommit}")
          newApp.push() // record this snapshot (optional)
          newApp.push 'latest'
        }
      }
    }
    stage('deploy') {
      container('kubectl') {
        sh "kubectl delete -f kubernetes/deployment.yaml"
        sh "kubectl apply -f kubernetes"
      }
    }
  }
}