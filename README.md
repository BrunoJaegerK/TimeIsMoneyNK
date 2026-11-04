# TimeIsMoneyNK

**TimeIsMoneyNK** es un plugin para servidores Minecraft Bedrock basados en Nukkit que recompensa a los jugadores por el tiempo que permanecen conectados al servidor.

El plugin permite configurar recompensas económicas por rango, controlar el tiempo de actividad de los jugadores y evitar que los usuarios reciban dinero mientras permanecen AFK.

Desarrollado por **BrunoJaegerK** para **DELUXEMINE NETWORK**.

## ✨ Características

- **Recompensas por tiempo conectado:** los jugadores reciben dinero automáticamente después de cumplir un intervalo de tiempo.
- **Sistema de rangos:** permite configurar recompensas diferentes según el rango del jugador.
- **Prioridad de rangos:** se aplica únicamente la recompensa del rango de mayor prioridad, sin acumular recompensas.
- **Sistema anti-AFK:** detecta la inactividad de los jugadores.
- **Control de pagos AFK:** permite habilitar o deshabilitar las recompensas para jugadores AFK.
- **Configuración personalizable:** permite modificar los importes, intervalos y ajustes del sistema.
- **Recarga de configuración:** permite recargar `config.yml` mediante un comando.
- **Integración con EconomyAPI:** utiliza EconomyAPI para entregar las recompensas.

## 📋 Requisitos

- Servidor Nukkit compatible.
- NukkitPetteriM1Edition (PM1E).
- Java 25.
- [EconomyAPI](https://github.com/Nukkit-coders/EconomyAPI).

## 📦 Instalación

1. Descarga el archivo `.jar` de TimeIsMoneyNK desde la sección Releases.
2. Coloca el archivo en la carpeta `plugins` de tu servidor.
3. Asegúrate de tener EconomyAPI instalado.
4. Inicia o reinicia el servidor.
5. Edita `plugins/TimeIsMoneyNK/config.yml` para personalizar las recompensas.

El archivo de configuración se genera al iniciar el plugin.

## 💰 Recompensas predeterminadas

| Rango | Recompensa | Intervalo |
|---|---:|---:|
| Default | $100 | 10 minutos |
| VIP | $150 | 10 minutos |
| VIP+ | $200 | 10 minutos |
| VIP Ext+ | $300 | 10 minutos |

Los importes y permisos pueden personalizarse mediante la configuración del plugin.

**Importante:** las recompensas no se acumulan entre rangos. Se utiliza únicamente el rango de mayor prioridad que tenga el jugador.

## 💤 Sistema AFK

TimeIsMoneyNK incluye un sistema de detección de inactividad.

- Detecta el movimiento y determinadas acciones del jugador.
- No considera la rotación de cámara como actividad.
- Permite establecer el tiempo necesario para considerar a un jugador AFK.
- Permite desactivar los pagos mientras el jugador está AFK.

Por defecto, los jugadores AFK no reciben recompensas.

## ⚙️ Comandos

| Comando | Descripción |
|---|---|
| `/tim` | Muestra información del plugin. |
| `/tim reload` | Recarga la configuración. |

## 🔐 Permisos

| Permiso | Descripción | Predeterminado |
|---|---|---|
| `timeismoney.info` | Permite utilizar el comando principal. | Todos |
| `timeismoney.admin` | Permite recargar la configuración. | Operadores |
| `timeismoney.rank.vip` | Permiso de recompensa VIP. | Configurable |
| `timeismoney.rank.vipplus` | Permiso de recompensa VIP+. | Configurable |
| `timeismoney.rank.vipext` | Permiso de recompensa VIP Ext+. | Configurable |

Los permisos de rangos pueden adaptarse a la configuración de LuckPerms.

## 🛠️ Compilación

El proyecto utiliza Maven y Java 25.

Para compilarlo desde la raíz del proyecto:

```bash
mvn clean package
```

El archivo compilado se encontrará en:

```text
target/TimeIsMoneyNK-1.0.0.jar
```

## ❤️ Créditos

Desarrollado con cariño para **DELUXEMINE NETWORK**.

**Autor:** BrunoJaegerK

**Repositorio:** https://github.com/BrunoJaegerK/TimeIsMoneyNK

