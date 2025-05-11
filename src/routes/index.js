const { Router } = require('express');
const router = Router();
const ResourceController = require('../controllers/ResourceController');

router.get('/api/test', (req, res) => {
    const data = {
        "id": "1",
        "message": "API is working"
    }
    res.status(200).json(data);
})

router.get('/api/list', ResourceController.list);
router.post('/api/buscar/:id', ResourceController.findById);
router.post('/api/agregar', ResourceController.add);
router.delete('/api/eliminar/:id', ResourceController.delete)
router.put('/api/actualizar/:id', ResourceController.update)

module.exports = router;