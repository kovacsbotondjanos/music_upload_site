docker image rm kovacsbotondjanos/musicupload-recommendation-engine:latest
docker image rm kovacsbotondjanos/musicupload-spring-app:latest
docker image rm kovacsbotondjanos/musicupload-react-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-spring-app:latest ./backend
docker push kovacsbotondjanos/musicupload-spring-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-react-app:latest ./frontend
docker push kovacsbotondjanos/musicupload-react-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-recommendation-engine:latest ./backend-recommendation-engine
docker push kovacsbotondjanos/musicupload-recommendation-engine:latest

minikube start --driver=docker --cpus=8 --memory=7000 --disk-size=50g

kubectl apply -f kubernetes-deployment/music-upload-mysql-db-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-minio-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-backend-recommendation-engine.yaml
kubectl apply -f kubernetes-deployment/music-upload-backend-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-frontend-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-redis-deployment.yaml

do {
    Clear-Host
    $statuses = kubectl get pods -o jsonpath="{range .items[*]}{.metadata.name}={.status.containerStatuses[*].ready}{'\n'}{end}"

    if (-not $statuses) {
        Write-Host "No pods found." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
        continue
    }

    $statusLines = $statuses -split "\n"
    $allReady = $true

    foreach ($line in $statusLines) {
        if ($line -match "=") {
            $name, $readyStates = $line -split "="
            $readyValues = $readyStates -split " "
            $readyCount = ($readyValues | Where-Object { $_ -eq "true" }).Count
            $totalCount = $readyValues.Length

            Write-Host "$name => $readyCount/$totalCount ready"

            if ($readyCount -ne $totalCount -or $totalCount -eq 0) {
                $allReady = $false
            }
        }
    }

    if (-not $allReady) {
        Write-Host "`nWaiting for all pods to be ready..."
        Start-Sleep -Seconds 2
    }

} until ($allReady)

Write-Output "All pods are Running!"

Start-Job -Name "MinikubeTunnel" -ScriptBlock { minikube tunnel }

Start-Job -Name "FrontendPortForward" -ScriptBlock { kubectl port-forward service/react-frontend-service 30001:80 }
