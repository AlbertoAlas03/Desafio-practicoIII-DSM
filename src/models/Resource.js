const { DataTypes } = require('sequelize');
const sequelize = require('../database/DB-connection');

const Recurso = sequelize.define('Recursos', {
    titulo: {
        type: DataTypes.STRING,
        allowNull: false
    },
    descripcion: {
        type: DataTypes.STRING,
        allowNull: false
    },
    tipo: {
        type: DataTypes.STRING,
        allowNull: false
    },
    enlace: {
        type: DataTypes.STRING,
        allowNull: false
    },
    imagen: {
        type: DataTypes.STRING,
        allowNull: false
    }
}, {
    tableName: 'Recursos',
    timestamps: true
});

module.exports = Recurso;
