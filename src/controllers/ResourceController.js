const Resource = require('../models/Resource');

exports.list = async (req, res) => {
    try {
        const resource = await Resource.findAll();
        res.status(200).json({
            recursos: resource
        })
    } catch (error) {
        console.log('No se pudo obtener los recursos: ', error)
        res.status(400).json({
            error: error.message
        });
    }
}

exports.findById = async (req, res) => {
    const id = parseInt(req.params.id)
    try {
        const resource = await Resource.findOne({ where: { id: id } });

        if (!resource) {
            return res.status(404).json({
                success: false,
                message: 'Recurso no encontrado'
            });
        }

        return res.status(200).json({
            recursos: resource
        });
    } catch (error) {
        console.log('No se encontro el recurso: ', error.message)
        return res.status(400).json({ message: error.message });
    }
}

exports.add = async (req, res) => {
    try {
        const { titulo, descripcion, tipo, enlace, imagen } = req.body
        await Resource.create({
            titulo: titulo,
            descripcion: descripcion,
            tipo: tipo,
            enlace: enlace,
            imagen: imagen
        });
        return res.status(200).json({ message: 'recurso creado con exito' });
    } catch (error) {
        console.log('No se pudo crear el recurso: ', error.message)
        return res.status(401).json({ message: error.message });
    }
}

exports.delete = async (req, res) => {
    try {
        await Resource.destroy({ where: { id: req.params.id } });
        return res.status(200).json({ message: 'recurso eliminado con exito' })
    } catch (error) {
        console.log('Error al eliminar el recurso: ', error.message)
        return res.status(400).json({ message: error.message })
    }
}

exports.update = async (req, res) => {
    try {
        const { titulo, descripcion, tipo, enlace, imagen } = req.body;
        const id = req.params.id;

        await Resource.update(
            {
                titulo,
                descripcion,
                tipo,
                enlace,
                imagen
            },
            {
                where: { id }
            }
        );

        return res.status(200).json({ message: 'Recurso actualizado con éxito' });
    } catch (error) {
        console.log('Error al actualizar el recurso: ', error.message);
        return res.status(400).json({ message: error.message });
    }
};
