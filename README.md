# app zello notificações

Projeto android que escuta as notificações que chegam no dispositivo em que estiver instalado.

## Classes

MainActivity
	Classe principal
	Método que trata as notificações:
		Atributo da classe: BroadcastReceiver onNotice 
		Método: onReceive()
		
AppNotificationListenerService
	NotificationListenerService que escuta notificações do dispositivo e encaminha o que chega para a classe MainActivity que possui atributo (BroadcastReceiver onNotice) que recebe as notificações enviadas por esse serviço.

AppBroadcastReceiver
	BroadcastReceiver que escuta notificações de ligações ao dispositivo móvel

SmsBroadcastReceiver
	BroadcastReceiver que escuta notificações de SMS enviados ao dispositivo móvel
	

## Pré-requisitos para rodar localmente

Ter instalado (configurado nas variáveis de ambiente):
- Java 8 (JAVA_HOME e PATH)
- Android Studio (v3.4.1 ou superior)
- Dependências no SDK Manager:
	- Android 9.0 (Pie) API 28
		- Android SDK Platform 28
		- Sources for Android 28


## Importando no Android Studio

Import project > selecionar diretório > next até o final
    
	
## Git

Baixar projeto:

    git clone https://github.com/herio/zello-notificacoes.git

Sobrescrever código local com o do repositório:

    git fetch --all
	git reset --hard origin/master
	
Atualizar código local mantendo suas atualizações
  
    git status
	git stash
	git pull
	git stash pop
	
Fazer commit:

    git add . --add todas alterações
    git commit -m "descrição" --faz commit repo local
    git push origin master --push repo remoto
    