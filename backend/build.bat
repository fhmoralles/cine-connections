call mvn clean package -DskipTests -Dquarkus.profile=%1 -Dquarkus.container-image.build=true

call C:\Users\fhmor\Desenvolvimento\doctl-1.104.0-windows-amd64\doctl.exe auth switch --context thinkproject2

call C:\Users\fhmor\Desenvolvimento\doctl-1.104.0-windows-amd64\doctl.exe registry login

call docker push registry.digitalocean.com/thinkproject/cineconnections-backend:%1
