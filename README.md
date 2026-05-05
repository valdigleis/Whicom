# Whicom

Whicom é um pequeno compilador para uma versão simplificada da clássica linguagem  [WHILE](https://www.cs.cmu.edu/~aldrich/courses/15-819O-13sp/resources/while-language.pdf), o compilador está sendo escrito usando técnicas de **análise preditiva recursiva** para a construção do Analisador Sintático (Parser).

## A Linguagem e o Compilador

WHILE é muito popular no estudo de especificações formais e corretude de programas, principalmente através do uso da [lógica de Hoare](https://en.wikipedia.org/wiki/Hoare_logic) (1934–2026). A gramática original considerada aqui para descrever os programas (as palavras) em WHILE é dada por:


#### Comandos:

<div align="center">

\<C\> &Rightarrow;  \<skip\>;\<C\> | \<id\> : \<E\>;\<C\> |  if(\<B\>)then{\<C\>} else{\<C\>}\<C\> | while(\<B\>){\<C\>}\<C\> | &lambda;

</div>

#### Expressões aritméticas:

<div align="center">

\<E\> &Rightarrow; \<num\>  | \<id\>  | \<E\> + \<E\> | \<E\> - \<E\>  | \<E\> * \<E\>  | ( \<E\> )

</div>

#### Expressões booleanas:

<div align="center">

\<B\>  &Rightarrow; true | false | \<E\> = \<E\> | \<E\> < \<E\> | not \<B\> | \<B\> and \<B\> | \<B\> or \<B\> | ( \<B\> )

</div>


#### Identificadores e números (Lexemas):

<div align="center">

\<id\>  &Rightarrow; a | b | c | . . .  x | y | z |  a\<id\> | b\<id\> | c\<id\> | . . .  | x\<id\> | y\<id\> | z\<id\>

\<num\> &Rightarrow; 0 | . . . | 9 | 0\<num\> | . . . | 9\<num\>

</div>

Como foi usado a estratégia de análise preditiva recursiva para este projeto foi necessário realizar algumas pequenas alterações na gramática, ficando da seguinte forma.
