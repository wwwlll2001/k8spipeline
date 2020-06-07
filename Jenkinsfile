#定义参数label，K8S启动的pod名称通过这个来制定
def label = "JenkinsPOD-${UUID.randomUUID().toString()}"
#定义jenkins的工作目录
def jenworkspace="/home/jenkins/workspace/${params.PROJECT}"
#maven项目缓存，提供编译速度
def mvnrepo="/tmp/repository"
#kubectl和docker执行文件，这个可以打到镜像里面，这边直接共享的方式提供
def sharefile="/tmp/sharefile"
#deployment等K8S的yaml文件目录
def k8srepo='/tmp/k8s_repos'

#cloud为我们前面提供的云名称，nodeSelector是K8S运行pod的节点选择
podTemplate(label: label, cloud: 'kubernetes-hiningmeng',nodeSelector: 'devops.k8s.icjl/jenkins=jnlp',
    containers: [
        containerTemplate(
            name: 'jnlp',
            image: 'registry-vpc.cn-hangzhou.aliyuncs.com/hiningmeng/jnlp:v1',
            ttyEnabled: true,
            alwaysPullImage: false),
        containerTemplate(
            name: 'jnlp-maven',
            image: 'jenkins/jnlp-agent-maven',
            //image:'ungerts/jnlp-agent-maven',
            ttyEnabled: true,
            alwaysPullImage: false,
            command: 'cat')
    ],
    volumes: [
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath:'/var/run/docker.sock'),
        persistentVolumeClaim(mountPath: "$mvnrepo", claimName: 'maven-repo-pvc', readOnly: false),
        persistentVolumeClaim(mountPath: "$sharefile", claimName: 'sharefile-repo-pvc', readOnly: false),
    ]
)
pipeline {

    node (label) {
        stage('Hello World'){
            container('jnlp'){
                echo "hello, world"
                sh "ln -s $sharefile/kubectl  /usr/bin/kubectl"
                sh "ln -s $sharefile/docker /usr/bin/docker"

            }
        }
        stage('Git Pull'){
            dir("$jenworkspace"){
                git branch: "${params.BRANCH}", changelog: false, credentialsId: 'jenkins-pull-key', poll: false, url: "${params.CODE_URL}"
            }
        }
        stage('Mvn Package'){
            container('jnlp-maven'){
                dir("$jenworkspace"){
                    sh "mvn clean install -Dmaven.test.skip=true  -U  -s  $sharefile/settings.xml"
                }
            }
        }
        stage('Docker build'){
            ...
        }
        stage('K8S Deploy'){
            ...
        }
    }
}
