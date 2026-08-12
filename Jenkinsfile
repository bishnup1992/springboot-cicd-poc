pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '429496639762'
        ECR_REPOSITORY = 'springboot-cicd-poc'
        ECR_REGISTRY = '429496639762.dkr.ecr.ap-south-1.amazonaws.com'
        EKS_CLUSTER = 'springboot-cicd-poc-cluster'
        HELM_CHART = 'springboot-cicd-chart'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube-Local') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=springboot-cicd-poc'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Generate Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()

                    echo "Image tag: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t ${ECR_REPOSITORY}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-credentials',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    sh '''
                        aws ecr get-login-password \
                          --region ${AWS_REGION} \
                        | docker login \
                          --username AWS \
                          --password-stdin ${ECR_REGISTRY}

                        docker tag \
                          ${ECR_REPOSITORY}:${IMAGE_TAG} \
                          ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}

                        docker push \
                          ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Deploy DEV') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-credentials',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    sh '''
                        aws eks update-kubeconfig \
                          --region ${AWS_REGION} \
                          --name ${EKS_CLUSTER}

                        helm upgrade --install springboot-dev \
                          ./${HELM_CHART} \
                          -f ./${HELM_CHART}/values-dev.yaml \
                          --set image.tag=${IMAGE_TAG} \
                          -n dev
                    '''
                }
            }
        }
        stage('Approve SIT') {
            steps {
                input message: "Deploy image ${IMAGE_TAG} to SIT?",
                      ok: 'Deploy to SIT'
            }
        }

        stage('Deploy SIT') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-credentials',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    sh '''
                        helm upgrade --install springboot-sit \
                          ./${HELM_CHART} \
                          -f ./${HELM_CHART}/values-sit.yaml \
                          --set image.tag=${IMAGE_TAG} \
                          -n sit
                    '''
                }
            }
        }

        stage('Approve PROD') {
            steps {
                input message: "Deploy image ${IMAGE_TAG} to PROD?",
                      ok: 'Deploy to PROD'
            }
        }

        stage('Deploy PROD') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-credentials',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    sh '''
                        helm upgrade --install springboot-prod \
                          ./${HELM_CHART} \
                          -f ./${HELM_CHART}/values-prod.yaml \
                          --set image.tag=${IMAGE_TAG} \
                          -n prod
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully. Image tag: ${env.IMAGE_TAG}"
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}