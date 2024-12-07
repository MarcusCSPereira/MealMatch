# MealMatch  🍲

## 🔎 Sobre o Projeto

Este projeto consiste em uma aplicação nativa desktop de criação, alteração, busca, publicação e compartilhamento de receitas culinárias desenvolvido utilizando principalmente Maven, Java 8, JavaFX e PostgreSQL. O projeto foi desenvolvido em uma proposta curricular no curso de Ciência da Computação - Bach. da Universidade Estadual do Sudoeste da Bahia (UESB) nas matérias de Banco de Dados I e Engenharia de Software que propuseram aos alunos a atividade de desenvolver uma aplicação que correlacionasse os assuntos abordados nas duas matérias ao longo do semestre.<br>
O software MealMatch é uma plataforma inovadora que busca facilitar a vida de pessoas que desejam otimizar seu tempo na cozinha, preparando refeições com base nos ingredientes que já possuem em casa. Muitas vezes, a falta de ideias ou a combinação errada de alimentos pode gerar desperdício e frustração no planejamento das refeições. O MealMatch resolve esse problema ao oferecer receitas personalizadas de acordo com os ingredientes disponíveis, permitindo que os usuários aproveitem ao máximo o que têm, sem a necessidade de compras adicionais. Além disso, o site proporciona uma experiência culinária eficiente e criativa, garantindo praticidade para quem busca refeições saborosas e rápidas de preparar.


##

## 🖼️ Imagens:
<mark> Caputuras com demonstração parcial do software em funcionamento: <mark/><br>

<img src="/app/src/main/resources/images/demo1.png">

##

## 🔨 Construído com:

* 💻 VsCode | Maven 
* 🛠️ Java 1.8.401 | JavaFX
* 🎲 PostgreSQL

##

## 🧰 Tecnologias Utilizadas:


![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

## 

## 🚀 Utilizando o projeto:

### Pré-requisitos:
Certifique-se de ter instalado em sua máquina:
1. **Java 8** ou superior: [Baixar Java](https://www.oracle.com/java/technologies/javase-downloads.html)
2. **Apache Maven**: [Baixar Maven](https://maven.apache.org/download.cgi)
3. **PostgreSQL**: [Baixar PostgreSQL](https://www.postgresql.org/download/)
4. **Git**: [Baixar Git](https://git-scm.com/downloads)

## 

### Passo a passo:

#### 1. Clone o repositório:
Abra o terminal e execute o comando abaixo para clonar o repositório:
```bash
git clone https://github.com/MarcusCSPereira/MealMatch.git .

```

#### 2. Acesse a pasta em que clonou o projeto:
```bash
cd <pasta-do-clone-do-projeto>
```

#### 3. Configure o ambiente:
##### Copie o arquivo .env.example para .env e preencha as informações necessárias, como credenciais do banco de dados PostgreSQL e configurações de conexão. Por exemplo:
```bash
cp .env.example .env
```

##### Abra o arquivo .env em qualquer editor de texto e configure as variáveis de ambiente:

```env
DATABASE_HOST=EXEMPLO
DATABASE_PORT=EXEMPLO
DATABASE_USER=EXEMPLO
DATABASE_PASSWORD=EXEMPLO
DATABASE_NAME=EXEMPLO
DATABASE_URL=EXEMPLO
EMAIL_PASSWORD_APP=EXEMPLO
```
##### *Para solicitar os dados do .env corretos envie um email para: [contato.marcuscspereira@gmail.com](mailto:contato.marcuscspereira@gmail.com)*

#### 4. Dentro do diretório que clonou o projeto acesse o diretório app/ e use o Maven para instalar as dependências do projeto, no terminal execute:
```bash
cd app
mvn clean install
```

#### 5. Execute o projeto:
##### Compile e execute o projeto. A classe principal (Main) está localizada em src/main/java/com/mealmatch/Main.java.
```bash
mvn exec:java -Dexec.mainClass="com.mealmatch.Main"
```

#### 6. Abra o aplicativo:
##### Após a execução do comando acima, o aplicativo será iniciado. Utilize a interface gráfica para navegar, criar e buscar receitas.

##

## 👨🏽‍💻 Autores do projeto:
![Marcus César Santos](https://github.com/MarcusCSPereira.png?size=50)[Marcus César Santos](https://github.com/MarcusCSPereira)<br>
![Hércules Sampaio Oliveira](https://github.com/HerculesDraycon.png?size=50)[Hércules Sampaio Oliveira](https://github.com/HerculesDraycon)<br>
![Franco Ribeiro Borba](https://github.com/FrancoBorba.png?size=50)[Franco Ribeiro Borba](https://github.com/FrancoBorba)<br>
![Lucca Nolasco Trancoso](https://github.com/LuccaNolasco.png?size=50)[Lucca Nolasco Trancoso](https://github.com/LuccaNolasco)<br>
![Kauan Rubem Matos](https://github.com/kauanrubem.png?size=50)[Kauan Rubem Matos](https://github.com/kauanrubem)

## 📝 Licença

Esse projeto está sob licença. Veja o arquivo [LICENSE](https://github.com/MarcusCSPereira/MealMatch/blob/main/LICENSE) para mais detalhes.


