'use strict'
const express = require('express');
const app = express();
const cors = require('cors');
const morgan = require('morgan');
require('dotenv').config();
const sequelize = require('./database/DB-connection');

const DB_test = async () => {
    try {
        await sequelize.authenticate();
        console.log('Conexión exitosa');
        await sequelize.sync(); // crea tablas si no existen
    } catch (error) {
        console.error('Error al conectar a SQL Server:', error);
    }
}

DB_test();

//settings
const port = process.env.PORT || 3002;
app.set('json spaces', 2);

//middlewares
app.use(morgan('dev'));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(cors());

//routes
app.use(require('./routes/index'));

//starting the server
app.listen(port, () => {
    console.log('Server listening on port ' + port)
})