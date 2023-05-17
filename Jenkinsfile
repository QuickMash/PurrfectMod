pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/QuickMash/PurrfectMod.git'
            }
        }
        
        stage('Build') {
            steps {
                sh 'gradlew build'
            }
        }
        
        stage('Test') {
            steps {
                sh 'gradlew test'
            }
        }
        
        stage('Package') {
            steps {
                sh 'gradlew shadowJar'
            }
        }
        
        stage('Publish') {
            steps {
                sh 'echo "Publishing the mod"'
                // Add your publish steps here, such as uploading the mod to a mod repository or artifact repository
            }
        }
    }
}
