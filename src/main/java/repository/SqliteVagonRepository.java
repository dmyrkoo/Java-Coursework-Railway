package repository;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реалізація {@link VagonRepository} з використанням SQLite.
 * Зберігає вагони у файловій базі даних через JDBC.
 */
public class SqliteVagonRepository implements VagonRepository {

    private static final Logger logger = LoggerFactory.getLogger(SqliteVagonRepository.class);

    private final Connection connection;

    /**
     * Створює репозиторій з підключенням до вказаного файлу бази даних.
     *
     * @param dbUrl JDBC URL бази даних (наприклад, {@code jdbc:sqlite:potiag.db})
     */
    public SqliteVagonRepository(String dbUrl) {
        try {
            this.connection = DriverManager.getConnection(dbUrl);
            logger.info("Підключено до бази даних: {}", dbUrl);
            initTable();
            seedDataIfEmpty();
        } catch (SQLException e) {
            logger.error("Помилка підключення до бази даних: {}", dbUrl, e);
            throw new RuntimeException("Не вдалося підключитися до БД: " + dbUrl, e);
        }
    }

    /**
     * Створює репозиторій з підключенням за замовчуванням ({@code jdbc:sqlite:potiag.db}).
     */
    public SqliteVagonRepository() {
        this("jdbc:sqlite:potiag.db");
    }

    /**
     * Створює таблицю {@code vagons}, якщо вона ще не існує.
     */
    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS vagons (
                    id                    INTEGER PRIMARY KEY,
                    type                  TEXT    NOT NULL,
                    komfortnist           INTEGER NOT NULL,
                    bagazh_kilkist        INTEGER NOT NULL,
                    klas_komfortu         TEXT,
                    kilkist_pasazhyriv    INTEGER,
                    riven_obslugovuvannya INTEGER,
                    personal_kilkist      INTEGER,
                    typ_pryznachennya     TEXT
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Таблицю 'vagons' ініціалізовано");
        } catch (SQLException e) {
            logger.error("Помилка створення таблиці 'vagons'", e);
            throw new RuntimeException("Не вдалося створити таблицю vagons", e);
        }
    }

    private void seedDataIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM vagons";
        String insertSql = """
                INSERT INTO vagons (id, type, komfortnist, bagazh_kilkist, klas_komfortu, kilkist_pasazhyriv, riven_obslugovuvannya, personal_kilkist, typ_pryznachennya)
                VALUES 
                (1, 'Pasazhyrsky', 8, 40, 'VIP', 20, 10, NULL, NULL),
                (2, 'Pasazhyrsky', 6, 60, 'KUPE', 36, 7, NULL, NULL),
                (3, 'Pasazhyrsky', 3, 80, 'PLATSKART', 54, 3, NULL, NULL),
                (4, 'Slyzhbovy', 7, 150, NULL, NULL, NULL, 5, 'Ресторан'),
                (5, 'Slyzhbovy', 4, 500, NULL, NULL, NULL, 2, 'Багажний')
                """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                logger.info("Таблиця 'vagons' порожня — додаємо тестові дані");
                stmt.executeUpdate(insertSql);
                logger.info("Додано 5 тестових вагонів через SQL INSERT");
            }
        } catch (SQLException e) {
            logger.error("Помилка перевірки/заповнення тестових даних", e);
        }
    }

    @Override
    public void saveVagon(Vagon vagon) {
        String sql = """
                INSERT OR REPLACE INTO vagons
                    (id, type, komfortnist, bagazh_kilkist,
                     klas_komfortu, kilkist_pasazhyriv, riven_obslugovuvannya,
                     personal_kilkist, typ_pryznachennya)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, vagon.getId());
            pstmt.setString(2, vagon.getType());
            pstmt.setInt(3, vagon.getKomfortnist());
            pstmt.setInt(4, vagon.getBagazhKilkist());

            if (vagon instanceof PasazhyrskyVagon pv) {
                pstmt.setString(5, pv.getKlasKomfortu().name());
                pstmt.setInt(6, pv.getKilkistPasazhyriv());
                pstmt.setInt(7, pv.getRivenObslugovuvannya());
                pstmt.setNull(8, Types.INTEGER);
                pstmt.setNull(9, Types.VARCHAR);
            } else if (vagon instanceof SlyzhbovyVagon sv) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.INTEGER);
                pstmt.setNull(7, Types.INTEGER);
                pstmt.setInt(8, sv.getPersonalKilkist());
                pstmt.setString(9, sv.getTypPryznachennya());
            }

            pstmt.executeUpdate();
            logger.info("Збережено вагон ID={} ({})", vagon.getId(), vagon.getType());
        } catch (SQLException e) {
            logger.error("Помилка збереження вагону ID={}", vagon.getId(), e);
            throw new RuntimeException("Не вдалося зберегти вагон ID=" + vagon.getId(), e);
        }
    }

    /**
     * Видаляє вагон з бази даних за його ідентифікатором.
     *
     * @param id ідентифікатор вагону для видалення
     */
    public void deleteVagon(int id) {
        String sql = "DELETE FROM vagons WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int deleted = pstmt.executeUpdate();
            logger.info("Видалено вагон ID={} з БД (рядків: {})", id, deleted);
        } catch (SQLException e) {
            logger.error("Помилка видалення вагону ID={}", id, e);
            throw new RuntimeException("Не вдалося видалити вагон ID=" + id, e);
        }
    }

    @Override
    public List<Vagon> getAllVagons() {
        String sql = "SELECT * FROM vagons ORDER BY id";
        List<Vagon> vagons = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vagon vagon = mapRowToVagon(rs);
                if (vagon != null) {
                    vagons.add(vagon);
                }
            }

            logger.info("Завантажено {} вагонів з БД", vagons.size());
        } catch (SQLException e) {
            logger.error("Помилка читання вагонів з БД", e);
            throw new RuntimeException("Не вдалося отримати вагони з БД", e);
        }

        return vagons;
    }

    @Override
    public void clearAll() {
        String sql = "DELETE FROM vagons";

        try (Statement stmt = connection.createStatement()) {
            int deleted = stmt.executeUpdate(sql);
            logger.info("Видалено {} записів з таблиці 'vagons'", deleted);
        } catch (SQLException e) {
            logger.error("Помилка очищення таблиці 'vagons'", e);
            throw new RuntimeException("Не вдалося очистити таблицю vagons", e);
        }
    }

    /**
     * Перетворює рядок {@link ResultSet} на об'єкт {@link Vagon}.
     *
     * @param rs результат запиту з поточним рядком
     * @return об'єкт вагону або {@code null}, якщо тип невідомий
     * @throws SQLException якщо виникає помилка читання даних
     */
    private Vagon mapRowToVagon(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        int id = rs.getInt("id");
        int komfortnist = rs.getInt("komfortnist");
        int bagazhKilkist = rs.getInt("bagazh_kilkist");

        return switch (type) {
            case "Pasazhyrsky" -> new PasazhyrskyVagon(
                    id, komfortnist, bagazhKilkist,
                    KlasKomfortu.fromString(rs.getString("klas_komfortu")),
                    rs.getInt("kilkist_pasazhyriv"),
                    rs.getInt("riven_obslugovuvannya")
            );
            case "Slyzhbovy" -> new SlyzhbovyVagon(
                    id, komfortnist, bagazhKilkist,
                    rs.getInt("personal_kilkist"),
                    rs.getString("typ_pryznachennya")
            );
            default -> {
                logger.warn("Невідомий тип вагону: {}", type);
                yield null;
            }
        };
    }

    /**
     * Закриває з'єднання з базою даних.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("З'єднання з БД закрито");
            }
        } catch (SQLException e) {
            logger.error("Помилка закриття з'єднання з БД", e);
        }
    }
}
