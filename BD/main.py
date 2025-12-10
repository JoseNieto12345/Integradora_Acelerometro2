import os
import sqlite3
from flask import Flask, request, jsonify, send_from_directory, Response
from werkzeug.utils import secure_filename
from datetime import date, datetime

# --- Configuración General ---
# Cambiamos el nombre de la DB para ser más específico
DB_PATH = 'steps_tracker_db.db'
TABLE_NAME = 'steps_log' # Nombre de la tabla para los pasos

# Las carpetas de subida de archivos (UPLOAD_FOLDER) y los archivos permitidos
# no son necesarios para la tabla de pasos, pero se mantienen si los quieres usar en el futuro.
UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# --- Funciones de Ayuda ---
def get_db_connection():
    """Establece la conexión con la base de datos SQLite."""
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def allowed_file(filename):
    """Verifica si la extensión del archivo está permitida."""
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# --- Creación de la Base de Datos (MODIFICADA) ---
def init_db():
    """
    Crea la tabla 'steps_log' con solo las columnas 'date' y 'steps'.
    """
    # Se mantiene la creación de la carpeta 'uploads' por si las rutas de archivos
    # (aunque no las uses ahora) se necesitan después.
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)

    conn = get_db_connection()

    # *** AQUÍ ESTÁ LA MODIFICACIÓN CLAVE: ***
    # Creamos una tabla simple con DATE (clave primaria) y STEPS.
    conn.execute(f'''
        CREATE TABLE IF NOT EXISTS {TABLE_NAME} (
            date TEXT PRIMARY KEY,
            steps INTEGER NOT NULL
        )
    ''')

    # Opcional: Insertar datos iniciales
    cursor = conn.cursor()
    if cursor.execute(f'SELECT COUNT(*) FROM {TABLE_NAME}').fetchone()[0] == 0:
        initial_data = [
            ("2025-12-05", 5400),
            ("2025-12-06", 12800),
            ("2025-12-07", 9100)
        ]
        cursor.executemany(f'INSERT INTO {TABLE_NAME} (date, steps) VALUES (?, ?)', initial_data)

    conn.commit()
    conn.close()

# --- Rutas de la API de Pasos ---
# Nota: Hemos quitado las rutas de archivos y lugares,
# y adaptado las rutas de CRUD a la tabla 'steps_log'.

# 1. GET (Leer Todos) - /api/steps
@app.route('/api/steps', methods=['GET'])
def get_steps_all():
    """Obtiene todos los registros de pasos."""
    conn = get_db_connection()
    rows = conn.execute(f'SELECT date, steps FROM {TABLE_NAME} ORDER BY date DESC').fetchall()
    conn.close()

    # La estructura de retorno es [{ "date": "YYYY-MM-DD", "steps": 0 }, ...]
    return jsonify([dict(row) for row in rows])

# 2. GET (Leer por Fecha) - /api/steps/YYYY-MM-DD
@app.route('/api/steps/<string:date_str>', methods=['GET'])
def get_steps_by_date(date_str):
    """Obtiene los pasos para una fecha específica."""
    conn = get_db_connection()
    row = conn.execute(f'SELECT date, steps FROM {TABLE_NAME} WHERE date = ?', (date_str,)).fetchone()
    conn.close()
    if row is None:
        return jsonify({'error': f'Registro de pasos no encontrado para la fecha {date_str}'}), 404
    return jsonify(dict(row))

# 3. POST (Crear/Actualizar) - /api/steps
# Se adapta para recibir los campos 'date' y 'steps' a través de form-data.
@app.route('/api/steps', methods=['POST'])
def create_or_update_steps():
    """Crea un nuevo registro o actualiza uno existente usando date como clave."""

    # Espera datos de formulario (multipart/form-data o x-www-form-urlencoded)
    date_str = request.form.get('date')
    steps_str = request.form.get('steps')

    if not date_str or not steps_str:
        return jsonify({'error': 'Faltan los campos "date" y/o "steps" requeridos en el formulario.'}), 400

    try:
        # Validación de tipos
        date.fromisoformat(date_str) # Chequea formato YYYY-MM-DD
        steps = int(steps_str)
    except ValueError:
        return jsonify({'error': 'Formato de fecha (YYYY-MM-DD) o pasos (entero) inválido.'}), 400

    conn = get_db_connection()

    # Usamos REPLACE INTO para actualizar si existe (clave duplicada) o insertar si no existe.
    conn.execute(
        f'REPLACE INTO {TABLE_NAME} (date, steps) VALUES (?, ?)',
        (date_str, steps)
    )
    conn.commit()
    conn.close()

    return jsonify({'message': f'Pasos para el {date_str} guardados/actualizados.'}), 201

# 4. DELETE (Borrar) - /api/steps/YYYY-MM-DD
@app.route('/api/steps/<string:date_str>', methods=['DELETE'])
def delete_steps(date_str):
    """Borra el registro de pasos para una fecha específica."""
    conn = get_db_connection()
    cursor = conn.execute(f'DELETE FROM {TABLE_NAME} WHERE date = ?', (date_str,))
    conn.commit()

    if cursor.rowcount == 0:
        conn.close()
        return jsonify({'error': f'Registro de pasos para {date_str} no encontrado.'}), 404

    conn.close()

    # Retorna la respuesta 204 No Content para borrado exitoso
    return Response(status=204)


# Ruta de Bienvenida (similar a la que creamos antes para depuración)
@app.route('/', methods=['GET'])
def home():
    """Muestra el estado del servidor y el contenido de la DB en HTML."""
    conn = get_db_connection()
    rows = conn.execute(f'SELECT date, steps FROM {TABLE_NAME} ORDER BY date DESC').fetchall()
    conn.close()

    steps_data = [dict(row) for row in rows]

    table_rows = "".join([
        f"""
        <tr class="hover:bg-gray-50 transition duration-150">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{entry['date']}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{entry['steps']}</td>
        </tr>
        """ for entry in steps_data
    ])

    html_content = f"""
    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>API de Pasos - Estado Optimizada</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <style>body {{ font-family: 'Inter', sans-serif; background-color: #f7fafc; }}</style>
    </head>
    <body class="p-8">
        <div class="max-w-4xl mx-auto bg-white p-6 rounded-xl shadow-lg">
            <h1 class="text-3xl font-bold text-gray-800 mb-2">✅ Servidor API de Pasos (Optimizado)</h1>
            <p class="text-gray-600 mb-6">Base de datos <span class="font-semibold text-blue-600">'{DB_PATH}'</span> conectada y sirviendo solo los campos <span class="font-mono text-pink-600">date</span> y <span class="font-mono text-pink-600">steps</span>.</p>

            <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Endpoints Disponibles:</h2>
            <ul class="list-disc list-inside space-y-1 text-gray-700">
                <li><code class="bg-gray-100 p-1 rounded">GET /api/steps</code>: Obtiene todos los registros.</li>
                <li><code class="bg-gray-100 p-1 rounded">POST /api/steps</code>: Guarda/Actualiza (requiere <span class="font-mono text-red-600">date</span> y <span class="font-mono text-red-600">steps</span> en form-data).</li>
                <li><code class="bg-gray-100 p-1 rounded">DELETE /api/steps/&lt;fecha&gt;</code>: Borra un registro por fecha.</li>
            </ul>

            <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Contenido de la Base de Datos ({TABLE_NAME})</h2>
            <div class="overflow-x-auto border border-gray-200 sm:rounded-lg">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha (TEXT - PK)</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Pasos (INTEGER)</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        {table_rows}
                    </tbody>
                </table>
            </div>
        </div>
    </body>
    </html>
    """
    return Response(html_content, mimetype='text/html')

if __name__ == '__main__':
    # Inicializa la DB (crea la tabla steps_log y los datos iniciales)
    init_db()
    # Ejecuta el servidor
    app.run(host='0.0.0.0', port=5000, debug=True)