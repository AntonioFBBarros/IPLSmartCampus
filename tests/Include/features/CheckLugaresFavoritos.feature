Feature: Lugares Favoritos
	Como utilizador autenticado
	Quero adicionar, remover, visualizar, alterar, filtrar e ordenar os meus lugares favoritos
	Para poder tirar melhor proveito da aplicação


Scenario: Adicionar local aos favoritos
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "ESTG - Edificio A"
	And Clico no botao "Favorite"
	And Clico no botao "Locais"
	And Clico no botao se existir "ALLOW" 
	And Vejo o text view "ESTG - Edificio A"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Ver locais favoritos
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao se existir "ALLOW" 
	And Seleciono o local "ESTG - Edificio A"
	And Vejo o text view "Qualidadedo Ar"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Remover local favorito
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Locais"
	And Clico no botao se existir "ALLOW" 
	And Seleciono o local "ESTG - Edificio A"
	And Clico no botao "Favorite"
	And Clico no botao "Locais"
	And Nao vejo o text view "ESTG - Edificio A" 
	And Faço logout
	Then Fecho a aplicacao

Scenario: Adicionar local aos favoritos nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "Dashboard"
	And Nao vejo o botao "Favorite"
	Then Fecho a aplicacao

Scenario: Ver locais favoritos nao autenticado
	Given Eu inicio a aplicacao
	And Nao vejo o botao "Locais"
	Then Fecho a aplicacao