kubectl create configmap server-config --from-file=application.properties
kubectl create -f AppDeploy.yml
sleep 20