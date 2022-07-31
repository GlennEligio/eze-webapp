# eze-webapp backend

**Running in local environment**

1. Checkout the master branch
2. Go inside the root directory
3. Install the dependencies by using _npm install_
4. Execute _npm run watc_
5. Execute _npm run dev_
6. At this point, you can start modifying the .ts files and the .js file will be updated and reexecuted

**NOTE:**

1. Make sure mongodb is running in localhost or 127.0.0.1 using port 27017

**Running in production server**

1. Checkout the master branch
2. Go inside the root directory
3. In production server, the environment present in _./config/dev.env_ except PORT must be present
4. Execute _npm run start_ in the production server where backend is deployed

**Todos**

1. Add logger
2. Add same pagination and sorting feature to other resources
