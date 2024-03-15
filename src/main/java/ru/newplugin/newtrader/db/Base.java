package ru.newplugin.newtrader.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class Base {
	/**
	 * Проверяет, есть ли в базе житель с нужным игроком
	 *
	 * @param name     имя игрока
	 * @param villager UUID жителя
	 */
	public static CompletableFuture<Boolean> contains(final String name, final String villager) {
		return CompletableFuture.supplyAsync(() -> {
			try (final Connection conn = ConnectionPool.getConnection();
				 final PreparedStatement s = conn.prepareStatement("SELECT nick FROM users WHERE nick = ? AND villager = ?")
			) {
				s.setString(1, name);
				s.setString(2, villager);

				try (final ResultSet set = s.executeQuery()) {
					if (set.next()) {
						return set.getString("nick") != null;
					}
				}
			} catch (final SQLException ignored) {
			}

			return false;
		});
	}

	/**
	 * Добавляет к указанному столбцу значение
	 *
	 * @param villager UUID жителя
	 * @param name     имя игрока
	 * @param set      столбец
	 * @param value    число, которое нужно прибавить
	 */
	public static void add(final String villager, final String name, final String set, final int value) {
		CompletableFuture.runAsync(() -> {
			try (final Connection conn = ConnectionPool.getConnection();
				 final PreparedStatement s = conn.prepareStatement("UPDATE users SET " + set + " = " + set + " + ? WHERE villager = ? AND nick = ?")
			) {
				s.setInt(1, value);
				s.setString(2, villager);
				s.setString(3, name);
				s.executeUpdate();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Выполняет любое обновление базы с заменой параметров
	 *
	 * @param text     запрос
	 * @param defaults значения для замены
	 */
	public static void update(final String text, final Object... defaults) {
		CompletableFuture.runAsync(() -> {
			try (final Connection conn = ConnectionPool.getConnection();
				 final PreparedStatement s = conn.prepareStatement(text)
			) {
				int i = 0;
				for (final Object def : defaults) {
					i++;
					s.setObject(i, def);
				}
				s.executeUpdate();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
