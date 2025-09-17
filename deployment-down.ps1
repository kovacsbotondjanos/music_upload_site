Stop-Job -Name "MinikubeTunnel"
Stop-Job -Name "FrontendPortForward"
Remove-Job -Name "MinikubeTunnel"
Remove-Job -Name "FrontendPortForward"

kubectl delete -f kubernetes-deplyment/music-upload-mysql-db-deployment.yaml
kubectl delete -f kubernetes-deplyment/music-upload-minio-deployment.yaml
kubectl delete -f kubernetes-deplyment/music-upload-backend-deployment.yaml
kubectl delete -f kubernetes-deplyment/music-upload-backend-recommendation-engine.yaml
kubectl delete -f kubernetes-deplyment/music-upload-frontend-deployment.yaml