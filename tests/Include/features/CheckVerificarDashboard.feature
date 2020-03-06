Feature: Verificar Dashboard
	Como utilizador
	Quero ver uma dashboard	
	Para que me possa informar do estado do ar de um edificio do IPL podendo alterar este edificio


Scenario: Verificar campo qualidade do ar
	Given Eu inicio a aplicacao
	When Verifico que o  campo "Qualidade do Ar" existe
	Then Fecho a aplicacao

Scenario: Verificar campo temperatura
	Given Eu inicio a aplicacao
	When Verifico que o  campo "Temperatura" existe
	Then Fecho a aplicacao

Scenario: Verificar campo humidade
	Given Eu inicio a aplicacao
	When Verifico que o  campo "Humidade" existe
	Then Fecho a aplicacao

Scenario: Verificar edificio A inicial
  Given Eu inicio a aplicacao
  When Vejo o text view "ESTG Edificio A Dashboard"
  Then Fecho a aplicacao

Scenario: Verificar botao perfil
  Given Eu inicio a aplicacao
  When Vejo o botao "PROFILE"
  Then Fecho a aplicacao
