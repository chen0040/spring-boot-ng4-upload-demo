# spring-boot-ng4-upload-demo

Demo project on how upload and process excel file in the spring boot + Web Socket + Angular 4

# Features

* Upload and process Excel file
* Allow Async callback and websocket to report progress on loading data into database (mocked database used here)
* Allow Large file upload and long running process using guava concurrency

# Usage

## If you are using IntelliJ

Open the project in IntelliJ

Go to src/java/com.github.chen0040.bootslingshot and right-click SpringSlingshotApplication and select "Run main()" in IntelliJ

Navigate to your web browser to http://localhost:9300


## Angular 4 Application

To run the angular application that communicate with the spring-boot-application:


```bash 
cd ng4-application
npm install
ng serve --proxy-config proxy.conf.json --host 0.0.0.0 --disable-host-check --port 8080
```

This will start the angular application at http://localhost:8080

After the angular site is launched, if you have not login before, click the login button on the page, you can authenticate using
any account below:

* username: admin password: admin
* username: user password: user

The websocket implementation that subscribe to "/topics/" + token + "/event" websocket topic of the spring-boot-application can be found in the app.service.ts and app.component.ts


### Upload Binary File



### Upload Image File

### Upload Excel File

Once at http://localhost:8080, click "Excel Sample Download" to download a csv sample, then click "Choose File" to load the
downloaded Excel sample,and then click "Upload Excel", you will notice that as the product is being saved on the remote server,
the web page keeps on updating the progress (done using websocket and sockjs). At the backend, the Thread.sleep is used to simulate
long running process.