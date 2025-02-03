#TODO: docker login when we are not already logged in on a remote server

docker build -t musicupload-spring-app ./backend

docker tag musicupload-spring-app:latest kovacsbotondjanos/musicupload-spring-app:latest

docker push kovacsbotondjanos/musicupload-spring-app:latest

kubectl apply -f backend/mysql-deployment.yaml
kubectl apply -f backend/minio-deployment.yaml
kubectl apply -f backend/spring-deployment.yaml