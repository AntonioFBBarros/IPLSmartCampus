Feature: Notificações da Plataforma
	Como utilizador autenticado
	Quero receber notificações enviadas pela plataforma e adiciona-las ao historico caso seja despoletado algum alarme específico nos meus lugares favoritos relacionado com o parâmetro sob análise: qualidade do ar

Scenario: Verificar notificacoes não tendo o local nos favoritos
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Dashboard"
	And Insiro no firebase a temperatura de '20' e humidade '60'
	And Insiro no firebase a temperatura de '40' e humidade '80'
	And Vejo que nao recebi uma notificacao
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verificar notificacoes
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Insiro no firebase a temperatura de '20' e humidade '60'
	And Insiro no firebase a temperatura de '40' e humidade '80'
	And Vejo que recebi uma notificacao
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Faço logout
	Then Fecho a aplicacao	
	
	Scenario: Verificar notificacoes com notificacoes desligadas
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "PROFILE"
	And Clico no botao ToggleNotifications
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Insiro no firebase a temperatura de '20' e humidade '60'
	And Insiro no firebase a temperatura de '40' e humidade '80'
	And Vejo que nao recebi uma notificacao
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Clico no botao "PROFILE"
	And Clico no botao ToggleNotifications
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verificar historico nova notificacao
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Insiro no firebase a temperatura de '20' e humidade '60'
	And Aply delay
	And Carrego no image button "historico de notificacoes"
	And Clico no botao "HBack"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Insiro no firebase a temperatura de '50' e humidade '60'
	And Aply delay
	And Clico no botao "PROFILE"
	And Verifico que tenho "1" notificacoes por ver
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Clico no botao "Favorite"
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verificar historico de notificações
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "PROFILE"
	And Verifico que o botao "historico de notificacoes" existe
	And Faço logout
	Then Fecho a aplicacao	

Scenario: Verificar notificacoes não autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "Dashboard"
	And Insiro no firebase a temperatura de '20' e humidade '65'
	And Insiro no firebase a temperatura de '20' e humidade '80'
	And Aply delay
	And Vejo que nao recebi uma notificacao
	Then Fecho a aplicacao