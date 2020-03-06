Feature: Percurso Diário
	Como utilizador autenticado
	Quero registar o meu percurso diário
	Para que tenha uma perspectiva sobre a qualidade do ar a que esteve exposto diariamente


Scenario: Adicionar local a MyExposure
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "DELETE MY EXPOSORE DATA" 
	And Vejo o text view "Account data deleted" 
	And Clico back
	And Clico no botao "MyExposure" 
	And Vejo o text view "Percurso Diario Vazio" 
	And Clico no botao "DashBoard" 
	And Clico no botao "SendExposure"
	And Clico no botao "YES"
	And Vejo o text view "Data sent" 
	And Clico back
	And Clico no botao "MyExposure" 
	And Vejo o group view "Grafico"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Ver percurso diario
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "MyExposure" 
	And Vejo o group view "Grafico"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Adicionar local ao meu percurso diario nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "DashBoard" 
	And Nao vejo o botao "SendExposure"
	Then Fecho a aplicacao
		
Scenario: Ver percurso diario nao autenticado
	Given Eu inicio a aplicacao 
	When Nao vejo o botao "MyExposure"
	Then Fecho a aplicacao




