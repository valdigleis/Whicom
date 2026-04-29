# Whicom

Whicom é um pequeno compilador para a clássica linguagem  WHILE, o compilador está sendo escrito usando técnicas de **análise preditiva recursiva** para a construção do Analisador Sintático (Parser).

## A Linguagem e o Compilador

While é muito popular no estudo de especificações formais e corretude de programas através do uso de lógica de Hoare (1934–2026). A gramática que descreve os programas (palavras) em WHILE é dada por:


#### Comandos

<div align="center">

\<cmd\> &Rightarrow; \<skip\>; | \<id\> : \<E\> | if(\<B\>)then{\<cmd\>} else{\<cmd\>} | while(\<B\>){\<cmd\>} | \<skip\>;\<cmd\> | \<id\> : \<E\>;\<cmd\> |  if(\<B\>)then{\<cmd\>} else{\<cmd\>}\<cmd\> | while(\<B\>){\<cmd\>}\<cmd\>

</div>

#### Expressões aritméticas

<div align="center">

\<E\> &Rightarrow; \<num\>  | \<id\>  | \<E\> + \<E\> | \<E\> - \<E\>  | \<E\> * \<E\>  | ( \<E\> )

</div>

#### Expressões booleanas

<div align="center">

\<B\>  &Rightarrow; true | false | \<E\> = \<E\> | \<E\> < \<E\> | not \<B\> | \<B\> and \<B\> | \<B\> or \<B\> | ( \<B\> )

</div>


#### Identificadores e números (terminais)

<div align="center">

\<id\>  &Rightarrow; a | b | c | . . .  x | y | z |  a\<id\> | b\<id\> | c\<id\> | . . .  | x\<id\> | y\<id\> | z\<id\>

\<num\> &Rightarrow; 0 | . . . | 9 | 0\<num\> | . . . | 9\<num\>

</div>
