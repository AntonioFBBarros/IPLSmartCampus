Feature: Selecionar Local
	Como utilizador
	Quero registar-me
	Para que possa ter credenciais para fazer login

Scenario: Verificar abrir e fechar menu
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Clico no botao "BACK"
	And Vejo o botao "Locais" 
	And Faço logout
	Then Fecho a aplicacao

Scenario: Verificar Campi Existentes no Menu
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Vejo o text view "ESTG - Edificio A"
	And Vejo o text view "1.B"
	And Vejo o text view "ESTG - Edificio D"
	And Clico no botao "BACK"
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verificar selecionar local
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais" 
	And Clico no botao "ALL"
	And Seleciono o local "1.B"
	And Vejo o label "ESSLei > Edificio A > Sala 1.B"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Verificar data dos dados muda ao inserir novos dados
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Verifico a data dos dados
	And Insiro no firebase a temperatura de '15' e humidade '65'
	And Vejo que a data dos dados mudou
	And Faço logout
	Then Fecho a aplicacao

Scenario: Verificar data de atualizacao muda ao inserir novos dados
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "A.S.1.2B"
	And Verifico a data de atualizacao
	And Insiro no firebase a temperatura de '15' e humidade '65'
	And Vejo que a data de atualizacao mudou
	And Faço logout
	Then Fecho a aplicacao
	
