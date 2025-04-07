# Snackbar Application

<p align="center">
	<img alt="Spring boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?style=for-the-badge&logo=Spring-Boot&logoColor=white">
	<img alt="Maven" src="https://img.shields.io/badge/Apache%20Maven-C71A36.svg?style=for-the-badge&logo=Apache-Maven&logoColor=white">
	<img alt="MongoDb" src="https://img.shields.io/badge/MongoDB-47A248.svg?style=for-the-badge&logo=MongoDB&logoColor=white">
	<img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=Docker&logoColor=white   ">
	<img alt="Ubuntu" src="https://img.shields.io/badge/Ubuntu-E95420.svg?style=for-the-badge&logo=Ubuntu&logoColor=white">
</p>

<h4 align="center"> 
  🍔 Snack bar 🍟
</h4>

<p align="center">
<a href="#about">About</a> •
<a href="#run"> Running the Application</a> •
<a href="#endpoints">Endpoints</a> •
<a href="#swagger">Swagger</a> •
<a href="#architecture">Application Architecture</a> •
<a href="#database">Database</a> •
<a href="#run-outside">Run application outside of the container</a> 
</p>
   
<p id="about">

## 💻 About the project
This is a backend-only application for managing products in a snackbar, following an hexagonal architecture.

The application is written in Java 21 using Spring Boot, built using Maven 3.9.9, uses MongoDB Atlas as database, and the application runs containerized using Docker 27.2 and on container images based on Ubuntu 24.04 (Noble Numbat) of amd64 architecture.

The Domain Drive Design (DDD) diagrams that define the main application flows are accessible in this Miro board: https://miro.com/app/board/uXjVLK2yXLA=/

The two videos that describe the user flow and the admin flow are hosted in Youtube in the following links (only visible through the links, not searcheable):

## Phase 3 Content

* Pipeline Video: https://youtu.be/aNIVJqTinHo
* Managed Database (MongoDB Atlas) Video: https://youtu.be/OfzypLYe4EU
* Amazon API Gateway + Lambda Authorizer Video: https://www.youtube.com/watch?v=kKcrDEj90PI

## Phase 2 Content

* Infrastructure Video 01: https://youtu.be/IboMg2fjcig
* Infrastructure Video 02: https://youtu.be/w421NQZvaNU
* Application Navigation Video: https://youtu.be/wC2Kxfhk16M
* Documentation in C4 Model Video: https://youtu.be/QQ4ISStS3WU
* Documentation in C4 Model Miro Board: https://miro.com/app/board/uXjVL0azFlU=/?share_link_id=904093921496
* Application User Guide: See [instructions.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/instructions.md) 
* Postman Collection: See [Fase2-Postman-Collection.json](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/Fase2-Postman-Collection.json)

## Phase 1 Content

* Project presentation and user flow: part 01 - https://youtu.be/T2oW0KYMC-U
* User flow: part 02 - https://youtu.be/y6bCACwyYLU 
* Admin flow: - https://youtu.be/14HJadw8JQ0 

</p>
   

<p id="run">

# Pipeline-based Provisioning

This project consists of: 1/A Java Spring Boot application hosted on **EKS** (Amazon Elastic Kubernetes Service) from AWS, 2/A MongoDB Atlas database, 3/An Amazon API Gateway and 4/A Lambda function working as an API Gateway Authorizer. All components can be provisioned in using GitHub Actions pipeline.

## Running the Pipeline Manually

To manually trigger the pipeline, follow these steps:

0. Ensure that these environment variables are correctly configured for the pipeline to work properly:

    AWS_ACCESS_KEY_ID
    AWS_DEFAULT_REGION
    AWS_SECRET_ACCESS_KEY
    AWS_SESSION_TOKEN
    MONGODBATLAS_ORG_PRIVATE_KEY
    MONGODBATLAS_ORG_PUBLIC_KEY
    ORG_ID

1. Navigate to the **snackbar-pipelines repository** where the pipeline is configured.
2. Click on the **"Actions"** tab located at the top of the repository.
3. In the list of workflows on the left, look for and select **"multi-stage-pipelines"**.
4. Once selected, click the **"Run workflow"** button on the right side of the page.
5. You can select the branch you want to run the pipeline on or use the main branch 
6. Click **"Run workflow"** to start the pipeline manually.

This will trigger the multi-stage pipeline and run the steps including build, test, and deploy stages based on the current code in the main branch.

To provision a homolog environment, commit to the homolog branch in the snackbar application repository. This way, the pipeline will provision a homolog docker image and homolog namespace in Kubernetes dedicated to this environment.

# Kubernetes Specifications

## 1. Technologies Used:
- **Backend API:** Java Spring Boot (Java 21, using Maven 3.9.9).
- **Container Orchestration:** AWS EKS v1.31.
- **Package Management:** Helm v3.15.3.
- **IaC:** Terraform v1.10.3.

## 2. System Components

### Backend (Java Spring Boot):
- Configured as a **Deployment** and implemented via Helm Chart.
   - Main container **snackbar** used for the API service.
- Communication via port **8080** for APIs exposed by the Kubernetes Service.
- Secret configuration stored in **Kubernetes Secret**:
  - **snackbar Secret:** Stores the database access credentials, connection string, JWT token, and its validity period.
- **Horizontal Pod Autoscaling (HPA)** enabled for scaling based on CPU and memory usage.
- Lineness and Readiness probes configured for health checks.


## 🏃‍♂️‍➡️ Running the Application
</p>

The pipeline outputs the ALB URL to access application in the "appready" step of the pipeline.

Remember that the application is backend only (no frontend).

<p id="endpoints">
   
## 📍API Endpoints
</p>

* Before testing the APIs, ensure you have [Postman](https://www.postman.com/) or similar installed on your system.
* The MongoDB "snackbar" database comes with two collections pre-loaded: "products" and "orders".

The application exposes the following REST API endpoints:

<div align="center">

| route               | description                                          
|----------------------|-----------------------------------------------------
| <kbd>Identity and Access Management (IAM) Endpoints</kbd>     | See [iam.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/iam/iam.md)
| <kbd>Productsv2 Endpoints</kbd>     | See [productsv2.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/product/productsv2.md)
| <kbd>Basket Endpoints</kbd>     | See [basket.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/order/basket.md)
| <kbd>Orders Refactored Endpoints</kbd>     | See [ordersRefactored.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/order/ordersRefactored.md)
| <kbd>Checkout Endpoints</kbd>     | See [checkout.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/checkout/checkout.md)
| <kbd>Payment Endpoints</kbd>     | See [payments.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/payment/payment.md)
</div>
| <kbd>Cooking Endpoints</kbd>     | See [cooking.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/cooking/cooking.md)
| <kbd>Pickup Endpoints</kbd>     | See [pickup.md](https://github.com/commskywalker/snackbar/blob/main/backend/src/main/java/com/snackbar/pickup/pickup.md)
</div>


<p id="swagger">
	
## 📄 Accessing the Swagger UI
</p>

You can access the Swagger UI to explore and test the APIs at:

http://host/swagger-ui.html

The host above changes depending on where you are running the application (e.g.: locally would be localhost:8080, on EKS with ALB would be the ALB endpoint)

<p id="architecture">
	
## 🏛️ Application Architecture
</p>

This application follows a hexagonal architecture, also known as ports and adapters architecture. The main components are:

- Domain: Contains the core business logic and entities (e.g., Product).
- Application: Defines the use cases and interfaces for the application (e.g., ProductService).
- Infrastructure: Implements the interfaces defined in the application layer (e.g., ProductRepository).
- Web: Handles the HTTP requests and responses (e.g., ProductController).

This architecture promotes separation of concerns and makes the application more modular and testable.

<p id="database">
	
## 🗃️ Database
</p>
This application uses MongoDB as its database. The MongoDB database name is "snackbar". It requires authentication and have pre-loaded an administrative user and a regular user, this last one used by the Java application with read/write access only to the "snackbar" database. 

<p id="run-outside">
	
## ❕If you need to Recompile/Run the Application outside of the container
</p>


### To run the application directly using Java and Maven:

1. Ensure you have Java 21 installed on your system. You can check your Java version by running:
<h6>💡If you don't have Java 21, download and install it from the official Oracle website or use a package manager. </h6>

   ```
   java -version
   ```


2. Make sure you have Maven 3.9.9 installed. You can check your Maven version by running:
   ```
   mvn -version
   ```

3. Navigate in your terminal to the project's root directory (where the `docker-compose.yml` file is located).
```bash
# Build the project using Maven:
$ mvn -f ./backend/pom.xml package

# Run the application:
$ java -jar ./backend/target/snackbar-0.0.1-SNAPSHOT.jar

```

### Infrastructure Architecture

Available at: [miro.com.br](https://miro.com/app/board/uXjVL0azFlU=/?track=true&utm_source=notification&utm_medium=email&utm_campaign=approve-request&utm_content=go-to-miro&lid=1v8fyk3ru6qu)
