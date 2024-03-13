package ru.newplugin.newtrader.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Base {
	public static Connection sql;
	private static final ExecutorService executor = Executors.newFixedThreadPool(5);

	/**
	 * Проверяет, есть ли в базе житель с нужным игроком
	 *
	 * @param name имя игрока
	 * @param villager UUID жителя
	 */
	public static CompletableFuture<Boolean> contains(final String name, final String villager) {
		return CompletableFuture.supplyAsync(() -> {
			try (final PreparedStatement s = sql.prepareStatement("SELECT nick FROM users WHERE nick = ? AND villager = ?")) {
				s.setString(1, name);
				s.setString(2, villager);
				final ResultSet set = s.executeQuery();
				if (set.next()) {
					return set.getString("nick") != null;
				}
			} catch (final SQLException ignored) {
			}

			return false;
		});
	}

	/**
	 * Добавляет к указанному столбцу значение
	 *
	 * @param name имя игрока
	 * @param villager UUID жителя
	 * @param set столбец
	 * @param value число, которое нужно прибавить
	 */
	public static void add(final String name, final String villager, final String set, final int value) {
		executor.submit(() -> {
			try (final PreparedStatement s = sql.prepareStatement("UPDATE users SET " + set + " = " + set + " + ? WHERE nick = ? AND villager = ?")) {
				s.setInt(1, value);
				s.setString(2, name);
				s.setString(3, villager);
				s.executeUpdate();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 *
	 * @param text запрос
	 * @param defaults значения для замены
	 */
	public static void update(final String text, final Object... defaults) {
		executor.submit(() -> {
			try (final PreparedStatement s = sql.prepareStatement(text)) {
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
