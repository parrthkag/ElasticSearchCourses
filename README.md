**Course Search Application**

A Spring Boot application that indexes course data in Elasticsearch and provides search functionality with filtering, sorting, pagination, autocomplete, and fuzzy search capabilities.

**Prerequisites**
- Java 17
- Maven
- Docker
- Docker Compose

**Setup Instructions**

1. **Start Elasticsearch**
   - Navigate to the project root directory
   - Run:
     ```bash
     docker-compose up -d
     ```
   - Verify Elasticsearch is running:
     ```bash
     curl http://localhost:9200
     ```
     You should see a JSON response with cluster information.

2. **Build and Run the Application**
   - Build the project:
     ```bash
     mvn clean install
     ```
   - Run the application:
     ```bash
     mvn spring-boot:run
     ```
     The application will be available at `http://localhost:8080`.

3. **Data Ingestion**
   - Sample course data is automatically loaded from `src/main/resources/sample-courses.json` on application startup.
   - To verify data ingestion, check the Elasticsearch index:
     ```bash
     curl http://localhost:9200/courses/_count
     ```

**API Endpoints**

1. **Search Courses**
   - **Endpoint**: `GET /api/search`
   - **Parameters**:
     - `q`: Search keyword (optional)
     - `minAge`: Minimum age (optional)
     - `maxAge`: Maximum age (optional)
     - `category`: Course category (optional)
     - `type`: Course type (ONE_TIME, COURSE, CLUB) (optional)
     - `minPrice`: Minimum price (optional)
     - `maxPrice`: Maximum price (optional)
     - `startDate`: ISO-8601 date (e.g., 2025-06-10T00:00:00Z) (optional)
     - `sort`: Sort order (upcoming, priceAsc, priceDesc) (default: upcoming)
     - `page`: Page number (default: 0)
     - `size`: Page size (default: 10)
   - **Example Requests**:
     ```bash
     # Basic search with keyword
     curl "http://localhost:8080/api/search?q=algebra"
     
     # Search with filters
     curl "http://localhost:8080/api/search?category=Science&minAge=10&maxAge=14&minPrice=100&maxPrice=200&startDate=2025-06-01T00:00:00Z&sort=priceAsc"
     
     # Paginated search
     curl "http://localhost:8080/api/search?q=physics&page=1&size=5"
     ```
   - **Example Response**:
     ```json
     {
       "total": 25,
       "courses": [
         {
           "id": "course_1",
           "title": "Introduction to Algebra",
           "category": "Math",
           "price": 199.99,
           "nextSessionDate": "2025-06-10T15:00:00Z"
         },
         ...
       ]
     }
     ```

2. **Autocomplete Suggestions**
   - **Endpoint**: `GET /api/search/suggest`
   - **Parameters**:
     - `q`: Partial title for suggestions
   - **Example Request**:
     ```bash
     curl "http://localhost:8080/api/search/suggest?q=phy"
     ```
   - **Example Response**:
     ```json
     [
       "Physics for Beginners",
       "Physical Science Basics"
     ]
     ```

3. **Fuzzy Search Example**
   - **Request**:
     ```bash
     curl "http://localhost:8080/api/search?q=dinors"
     ```
   - **Behavior**: This will match "Dinosaurs 101" despite the typo due to fuzzy matching on the title field.

**Running Tests**
1. Ensure Testcontainers and Docker are set up.
2. Run tests:
   ```bash
   mvn test
   ```


<pre>
	<code> 
		```
src/
├── main/
│   ├── java/com/example/coursesearch/
│   │   ├── config/CourseInitializer.java
│   │   ├── controller/CourseSearchController.java
│   │   ├── document/CourseDocument.java
│   │   ├── service/CourseSearchService.java
│   │   └── CourseSearchApplication.java
│   └── resources/
│       ├── application.yml
│       ├── elasticsearch-settings.json
│       └── sample-courses.json
├── test/
│   └── java/com/example/coursesearch/CourseSearchIntegrationTest.java
docker-compose.yml
pom.xml
README.md
		``` 
	</code> 
</pre>


**Notes**

1.The application uses Spring Data Elasticsearch for simplified interaction with Elasticsearch.

2.Fuzzy search is implemented with automatic fuzziness for the title field.

3.Autocomplete uses Elasticsearch's completion suggester.

4.Integration tests use Testcontainers to ensure reliable testing with an ephemeral Elasticsearch instance.

5.The sample data in sample-courses.json contains 50 varied course entries, covering different categories, types, ages, prices, and session dates.

