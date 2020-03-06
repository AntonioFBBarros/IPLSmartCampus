Feature:	
Como utilizador autenticado
Quero contribuir para a plataforma de suporte centralizado 
enviando os dados de temperatura e humidade relativos a um local 
Para que possa actualizar os dados da aplicação

Scenario: Verificar enviar dados
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao se existir "ALLOW" 
	And Clico no botao "ALL"
	And Seleciono o local "1.B"
	And Clico no botao "SEND DATA" 
	And Clico no botao "YES" 
	And Vejo o text view "Data sent" 
	And Clico back
	And Vejo o text view "Temperature Recebida" 
	And Vejo o text view "Humidade Recebida" 
	And Faço logout
	Then Fecho a aplicacao


Scenario: Verificar enviar dados nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "Dashboard"
	And Nao vejo o botao "SEND DATA"
	Then Fecho a aplicacao