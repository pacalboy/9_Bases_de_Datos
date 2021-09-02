# 9_Bases_de_Datos
Gestión de Bases de Datos.

## MongoDB
Realiza una práctica siguiendo el tutorial de [java-quick-start](https://github.com/mongodb-developer/java-quick-start) y responde a las siguientes preguntas:  
  

- ¿Cómo se llaman las "carpetas" en MongoDB?  
Collections (colecciones)  

- ¿Qué tipo de BD es MongoDB?  
Es un gestor de base de datos orientado a documentos  

- ¿Cuál es la extensión de los ficheros/objetos que almacena MongoDB?  
.bson, que es una especificación similar a .json 
  
- Siguiendo la notación JSON, ¿cuál es el caracter delimitador de campo?  
La coma `,` 
 
- Siguiendo la notación JSON, ¿cuál es el caracter que separa el identificador del campo de su valor?  
Los dos puntos `:`

- ¿Cuál es la diferencia entre una base de datos relacional y una base de datos no relacional?  
Una base de datos **relacional** está **estructurada**, y las tablas están
relacionadas mediante claves primarias, y en las que los datos siguen una
estructura fija.
En la base de datos **no relacional** no están estructuradas de esa forma,
tienen un **esquema dinámico**. Eso nos permite que podamos guardar en la
misma colección *objetos* diferentes, una base de datos de personas podría
contener animales.  

- Indica el enlace de descarga de la herramienta utilizada para MongoDB  
https://www.mongodb.com/try/download/community

- ¿Cuál es la dependencia necesaria para trabajar con Mongo DB en Java?  
~~~
<groupId>org.mongodb</groupId>
<artifactId>mongo-java-driver</artifactId>
<version>3.12.8</version>
~~~  
- ¿Cuál es el número de puerto por donde se comunica la aplicación de Mongo?  
  localhost:27017
  
- ¿Qué botón es necesario pulsar en la interfaz para crear una nueva base de datos?  
El botón + de abajo a la izquierda, o en el botón verde CREATE DATABASE
que solo aparece si no tienes nada seleccionado.  
![](/1.MongoDB/1.png)

- ¿Y una colección?  
El botón + que está en la base de datos, o en el botón verde CREATE
COLLECTION que solo aparece si tienes una base de datos seleccionada.  
![](/1.MongoDB/2.png)

- ¿Cómo se añade un nuevo objeto a la colección usando la interfaz gráfica de MongoDB?  
  En el botón verde ADD DATA, opción Insert Document. En la pantalla que
aparece se añade el nuevo objeto escribiendo sus atributos.
![](/1.MongoDB/3.png)

- Crea una aplicación en Java que se conecte a MongoDB. Crea una nueva base de datos llamada "Biblioteca". Dentro de esta base de datos crea una colección llamada "Libros".  

La base de datos se puede crear en MongoDB siguiendo los pasos antes
descritos, o desde la misma aplicación Java se creará al conectarse si esta
no existe.
~~~
try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")){
MongoDatabase libraryDB = mongoClient.getDatabase("library");
MongoCollection<Document> booksCollection = libraryDB.getCollection("books");
}
~~~  

- Inserta 200 libros en la colección. Los libros son unas entidades que tienen los siguientes atributos: ISBN (es el identificador), título, autora y año de publicación. Tu aplicación debe generar libros de forma aleatoria utilizando la librería Faker que dispone de un método book() que permite generar los atributos mencionados, el año puedes generarlo con Random, debe ser un entero entre 1900 y 2021.  

Clase Book:
~~~
/**
 * Book represented by title, author and publication year.
 * @author Álvaro
 * @version Date: 26/05/2021
 */
public class Book {
    private String title;
    private String author;
    private int year;

    private Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }
    
    /**
     * Generates a random book using Faker library.
     * @return Book
     */
    public static Book randomBook(){
        Faker faker = new Faker();
        Random r = new Random();
        String title = faker.book().title();
        String author = faker.book().author();
        int year = r.nextInt(122)+1900;
        return new Book(title, author, year);
    }
}
~~~  
  
Clase CreateInsertBook:
~~~
/**
 * Creates and inserts books in a MongoDB database.
 * @author Álvaro
 * @version Date: 26/05/2021
 */
public class CreateInsertBooks {

    /**
     * Generates books documents.
     * @return document
     */
    private static Document generateNewBook() {
        Book b = Book.randomBook();
        return new Document("_id", new ObjectId()).append("title", b.getTitle())
                .append("author", b.getAuthor())
                .append("year", b.getYear());
    }
    
    /**
     * Inserts a hundred books into a collection.
     * @param booksCollection target collection
     */
    private static void insertManyDocuments(MongoCollection<Document> booksCollection) {
        List<Document> books = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            books.add(generateNewBook());
        }
        booksCollection.insertMany(books, new InsertManyOptions().ordered(false));
        System.out.println("100 books have been inserted.");
    }
    
    /**
     * Main to run.
     * @param args the command line arguments, not needed
     */
    public static void main(String[] args) {
        
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase libraryDB = mongoClient.getDatabase("library");
            MongoCollection<Document> booksCollection = libraryDB.getCollection("books");
            
            insertManyDocuments(booksCollection);
            insertManyDocuments(booksCollection);
        }
    }
}

~~~

- Consulta los títulos que se han publicado después del año 2000.  
- Consulta los títulos escritos por autores que comienzan por la letra 'P'.
- Consulta el primer libro publicado en 2021.

Clase ReadBooks:
~~~
/**
 * Search examples for MongoDB.
 * @author Álvaro
 * @version Date: 26/05/2021
 */
public class ReadBooks {

    /**
     * Find titles with publication year greater than year given as parameter.
     * @param booksCollection target collection
     * @param year publication year
     */
    private static void findTitleByYear(MongoCollection<Document> booksCollection, int year){
        List<Document> booksList = booksCollection.find(gte("year", year))
                .projection(fields(excludeId(), include("title", "year")))
                .into(new ArrayList<>());
        for (Document d : booksList) {
            System.out.println(d.toJson());
        }
    }
    
    /**
     * Find titles whose author name has initial the character given as parameter.
     * This method use a pattern.
     * @param booksCollection target collection
     * @param c initial
     */
    private static void findByNameInitialPattern(MongoCollection<Document> booksCollection, char c) {
        Pattern pattern = Pattern.compile("^" + c + ".*$", Pattern.CASE_INSENSITIVE);
        
        FindIterable<Document> iterable = booksCollection.find(eq("author", pattern))
                .projection(fields(excludeId(), include("title", "author")));
        MongoCursor<Document> cursor = iterable.iterator();
        
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
    }
    
    /**
     * Find titles whose author name has initial the character given as parameter.
     * This method use a pattern.
     * @param booksCollection target collection
     * @param c initial
     */
    private static void findByNameInitial(MongoCollection<Document> booksCollection, char c) {
        List<Document> booksList = booksCollection.find(and(gte("author", c), lte("author", (char)(c+1))))
                .projection(fields(excludeId(), include("title", "author")))
                .into(new ArrayList<>());
        
        for (Document d : booksList) {
            System.out.println(d.toJson());
        }
    }
    
    /**
     * Find first book published in year given as parameter.
     * @param booksCollection target collection
     * @param year publication year
     */
    private static void findFirstOfYear(MongoCollection<Document> booksCollection, int year){
        Document d = booksCollection.find(eq("year", year)).first();
        System.out.println(d.toJson());
    }
    
    
    /**
     * Main to run.
     * @param args the command line arguments, not needed
     */
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {

            MongoDatabase libraryDB = mongoClient.getDatabase("library");
            MongoCollection<Document> booksCollection = libraryDB.getCollection("books");

            System.out.println("\nFind all books with a publication year after 2000:");
            findTitleByYear(booksCollection, 2000);
            
            System.out.println("\nFind all books whose author name has initial P:");
            findByNameInitial(booksCollection, 'P');
            
            System.out.println("\nFind all books whose author name has initial P (with PATTERN):");
            findByNameInitialPattern(booksCollection, 'P');
            
            System.out.println("\nFind the first book published in 2021:");
            findFirstOfYear(booksCollection, 2021);
        
        }    
    }
    
}


~~~

~~~
97/100
~~~

## MySQL_Connector

Usando el ejemplo de proyecto  [https://github.com/jayden-lee/mysql-jdbc-example](https://github.com/jayden-lee/mysql-jdbc-example)  vamos a trabajar sobre dos ficheros:  

**connection/CommonConnection.java**  
Se conecta a la base de datos de MySQL y nos muestra información genérica de la BD realizando la consulta "show processlist" que no creo que la hayáis visto en BD pero si os interesa pues la investigáis. Para este ejercicio debéis:  
1. Comentar la clase completa. Especificando qué hace cada línea de código. Debéir usar la documentación oficial para saber qué hace cada método.  
2. Modificar la clase para que se conecte a vuestro servidor MySQL. Cuando ejecutéis tened en cuenta que debéis tener levantado el server con XAMP, si no activáis el servicio no conseguiréis conectaros.  
3. Adjuntar captura de los resultados obtenidos por consola al conectaros a la BD.  
~~~
public class CommonConnection {
    //Atributos para conectarse a la BD
    //La url comienza por el driver JDBC, y termina con el parámetro de la zona horaria
    private static final String url = "jdbc:mysql://localhost:3306/kickstarter?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "qwerty";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) {
        try {
            loadJdbcDriver();

            createConnection();

            createStatementAndGetResultSet();

            traverseAndProcessResultSet();

            closeResources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * El driver se conecta al motor de Base de datos
     * 
     * @throws ClassNotFoundException
     */
    private static void loadJdbcDriver() throws ClassNotFoundException {
        // 5.X -> com.mysql.jdbc.Driver
        // 8.X -> com.mysql.cj.jdbc.Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    /**
     * Se conecta la aplicación a la BD usando los atributos
     *
     * @throws SQLException
     */
    private static void createConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    /**
     * Se crea una sentencia con la conexión, se ejecuta y se guarda en resultSet
     *
     * @throws SQLException
     */
    private static void createStatementAndGetResultSet() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT id, nombre FROM patrocinador WHERE mail LIKE '%gmail.com';");
    }

    /**
     * Imprime los datos de la consulta
     *
     * @throws SQLException
     */
    private static void traverseAndProcessResultSet() throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        // Se almacena el número de tuplas que tiene la consulta
        int columnCount = resultSetMetaData.getColumnCount();

        // Imprime el nombre de las columnas
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(resultSetMetaData.getColumnName(i) + "\t");
        }

        System.out.println();

        // Imprime las tuplas 
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getString(i) + " \t");
            }

            System.out.println();
        }
    }

    /**
     * Se cierran los recursos y conexiones
     */
    private static void closeResources() {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(connection);
    }
}

~~~
![](/2.MySQL_Connector/CommonConnection_Console.png)
  
**query/CountRecord.java**  
Se conecta a la BD MySQL y realiza dos consultas a una tabla (vosotros debéis poner una tabla de vuesta base de datos) con el objetivo de calcular el número de entradas de la tabla. La primera forma de calcular este número es un poco _paseosa_ y la segunda sí es eficiente. Debéis:  

1. Comentar la clase completa. Especificando qué hace cada línea de código. Debéir usar la documentación oficial para saber qué hace cada método.  
2. Modificar la clase para que se conecte a vuestro servidor MySQL y consulte a una tabla de vuestra BD. Cuando ejecutéis tened en cuenta que debéis tener levantado el server con XAMP, si no activáis el servicio no conseguiréis conectaros.  

3. Adjuntar captura de los resultados obtenidos por consola al conectaros a la BD y realizar la consulta.  

~~~
public class CountRecord {
    //Atributos para conectarse a la BD
    //La url comienza por el driver JDBC, y termina con el parámetro de la zona horaria
    private static final String url = "jdbc:mysql://localhost:3306/kickstarter?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "qwerty";

    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = JdbcUtils.getConnection(url, user, password);

            int totalRows = getTotalRows();
            System.out.println("Total Rows : " + totalRows);

            int totalRows2 = getTotalRows2();
            System.out.println("Total Rows : " + totalRows2);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            JdbcUtils.closeConnection(connection);
        }
    }

    /**
     * Cuenta las tuplas de una consulta.
     * No las cuenta, obtiene el número de la última tupla.
     * 
     * @return número de tuplas
     * @throws SQLException 
     */
    private static int getTotalRows() throws SQLException {
        //Guarda la consulta en un String
        String sql = "SELECT * FROM kickstarter.proyecto";

        int totalRows;
        //Crea una sentencia con una conexion
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            //Guarda el resultado de la consulta guardada en el String
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                //Apunta a la útlima tupla
                resultSet.last();
                //Obtiene el número de la tupla a la que está apuntando (la útlima)
                totalRows = resultSet.getRow();
            } catch (SQLException e) {
                throw e;
            }
        }

        return totalRows;
    }
    /**
     * Cuenta las tuplas de una consulta.
     * 
     * @return número de tuplas
     * @throws SQLException
     */
    private static int getTotalRows2() throws SQLException {
        //Guarda la consulta en un String
        String sql = "SELECT count(*) FROM kickstarter.proyecto";

        int totalRows = 0;

        //Intenta establecer una conexion con la consulta guardada
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                //Ejecuta y guarda el resultado de la consulta 
                ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                //Guarda el número de tuplas de la consulta mediante un método directo
                totalRows = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw e;
        }

        return totalRows;
    }
}
~~~
![](/2.MySQL_Connector/CountRecord_Console.png)

  
Preguntas:  
- ¿Cómo se llama la dependencia que nos permite conectarnos a MySQL?  
mysql-connector-java
~~~
<groupId>mysql</groupId>  
<artifactId>mysql-connector-java</artifactId>  
<version**>8.0.25</version>  
~~~

- ¿Qué versión utiliza el proyecto que hemos descargado? ¿Es la versión más actual?  
Usa la 8.0.13. La más actual es la 8.0.25.

- Investiga cuál es la dependencia necesaria para conectarse a una BD SQLite
~~~
<groupId>org.xerial</groupId>  
<artifactId>sqlite-jdbc</artifactId>  
<version>3.34.0</version>  
~~~

~~~
90/100
~~~
