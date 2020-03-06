Feature: Como utilizador
	Quero atualizar o dashboard
	Para que me possa obter informação actualizada da qualidade do ar
	
Scenario: Verificar localização
	Given Eu inicio a aplicacao
	When Verifico que o header tem o texto "ESTG  Edificio A"
	Then Fecho a aplicacao
	
Scenario: Verificar botão atualizar
	Given Eu inicio a aplicacao
	When Vejo o botao atualizar
	Then Fecho a aplicacao
	
Scenario: Verificar timestamp dos dados presente
	Given Eu inicio a aplicacao
	When Verifico que vejo o timestamp dos dados
	Then Fecho a aplicacao
	
Scenario: Verificar que o refresh funciona
	Given Eu inicio a aplicacao
	When Vejo o timestamp da ultima atualizacao
	And Clico no botao atualizarDados
	And Vejo que o timestamp mudou
	Then Fecho a aplicacao