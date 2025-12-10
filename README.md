# [Conta+]

> **Proyecto Integrador - Desarrollo de Aplicaciones Móviles**
>
> **Semestre:** [4°D]
> **Fecha de entrega:** 11 de Diciembre

---

## Equipo de Desarrollo

| Nombre Completo | Rol / Tareas Principales | Usuario GitHub |
| :--- | :--- | :--- |
| [Nieto Jaimes José Emmanuel] | [Creación de repositorio, arquitectura base (MVVM), y configuración de dependencias.] | @usuario1 |
| [Mojica Pérez German Alexander] | [Desarrollo del Backend (API REST en Flask/SQLite) y manejo de conectividad (Retrofit).] | @usuario2 |
| [Hernández Soto Gustavo Daniel] | [Implementación de la lógica del Sensor de Pasos y la Vista de historial (Jetpack Compose).] | @usuario3 |

---

## Descripción del Proyecto

**¿Qué hace la aplicación?**
[Este proyecto, denominado Conta+, es una aplicación de seguimiento de actividad física que mide y registra los pasos diarios del usuario utilizando el sensor de pasos integrado en el dispositivo Android. La aplicación centraliza la gestión de datos mediante una API REST externa, permitiendo a los usuarios visualizar su historial de actividad de forma persistente. La interfaz está construida con Jetpack Compose para ofrecer una experiencia de usuario moderna y fluida.]

**Objetivo:**
Demostrar la implementación de una arquitectura robusta en Android utilizando servicios web y hardware del dispositivo.

---

## Stack Tecnológico y Características

Este proyecto ha sido desarrollado siguiendo estrictamente los lineamientos de la materia:

* **Lenguaje:** Kotlin 100%.
* **Interfaz de Usuario:** Jetpack Compose.
* **Arquitectura:** MVVM (Model-View-ViewModel).
* **Conectividad (API REST):** Retrofit.
    * **GET:** [Obtiene el historial completo de registros diarios de pasos de la API ]
    * **POST:** [Envía los pasos totales registrados para el día actual. La API utiliza la fecha como clave única para insertar un nuevo registro o actualizar el existente si ya se han enviado pasos para esa fecha.]
    * **UPDATE:** [Implícito en la operación POST/REPLACE. Si se registra una nueva cantidad de pasos para una fecha que ya existe, el valor anterior es sobrescrito con el nuevo total.]
    * **DELETE:** [Permite borrar el registro de pasos de un día específico, si el usuario decide eliminar un dato incorrecto de su historial.]
* **Sensor Integrado:** [Acelerómetro]
    * *Uso:* [ El sensor de pasos se utiliza para escuchar los eventos de conteo de pasos en segundo plano (o primer plano) y actualizar el valor acumulado del día.]

---

## Capturas de Pantalla

[Coloca al menos 3 (investiga como agregarlas y se vean en GitHub)]

| Pantalla de Inicio | Operación CRUD | Uso del Sensor |
| :---: | :---: | :---: |
| ![Inicio](url_imagen) | ![CRUD](url_imagen) | ![Sensor](url_imagen) |

---

## Instalación y Releases

El ejecutable firmado (.apk) se encuentra disponible en la sección de **Releases** de este repositorio.

[Liga correctamente tu link de releases en la siguiente sección]

1.  Ve a la sección "Releases" (o haz clic [aquí](link_a_tus_releases)).
2.  Descarga el archivo `.apk` de la última versión.
3.  Instálalo en tu dispositivo Android (asegúrate de permitir la instalación de orígenes desconocidos).