package dev.guruprasath.feeledger.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile StudentDao _studentDao;

  private volatile PaymentDao _paymentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `students` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `batch` TEXT NOT NULL, `guardianPhoneEnc` TEXT NOT NULL, `monthlyFeePaise` INTEGER NOT NULL, `dueDay` INTEGER NOT NULL, `startMonth` INTEGER NOT NULL, `active` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `studentId` INTEGER NOT NULL, `monthKey` INTEGER NOT NULL, `amountPaise` INTEGER NOT NULL, `method` TEXT NOT NULL, `utrEnc` TEXT, `paidAt` INTEGER NOT NULL, FOREIGN KEY(`studentId`) REFERENCES `students`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_studentId` ON `payments` (`studentId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_monthKey` ON `payments` (`monthKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6d368eeeaa9f215ef3492d81a40e1c58')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `students`");
        db.execSQL("DROP TABLE IF EXISTS `payments`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsStudents = new HashMap<String, TableInfo.Column>(9);
        _columnsStudents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("batch", new TableInfo.Column("batch", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("guardianPhoneEnc", new TableInfo.Column("guardianPhoneEnc", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("monthlyFeePaise", new TableInfo.Column("monthlyFeePaise", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("dueDay", new TableInfo.Column("dueDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("startMonth", new TableInfo.Column("startMonth", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudents.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStudents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStudents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStudents = new TableInfo("students", _columnsStudents, _foreignKeysStudents, _indicesStudents);
        final TableInfo _existingStudents = TableInfo.read(db, "students");
        if (!_infoStudents.equals(_existingStudents)) {
          return new RoomOpenHelper.ValidationResult(false, "students(dev.guruprasath.feeledger.data.local.StudentEntity).\n"
                  + " Expected:\n" + _infoStudents + "\n"
                  + " Found:\n" + _existingStudents);
        }
        final HashMap<String, TableInfo.Column> _columnsPayments = new HashMap<String, TableInfo.Column>(7);
        _columnsPayments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("studentId", new TableInfo.Column("studentId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("monthKey", new TableInfo.Column("monthKey", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("amountPaise", new TableInfo.Column("amountPaise", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("method", new TableInfo.Column("method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("utrEnc", new TableInfo.Column("utrEnc", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("paidAt", new TableInfo.Column("paidAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPayments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPayments.add(new TableInfo.ForeignKey("students", "CASCADE", "NO ACTION", Arrays.asList("studentId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPayments = new HashSet<TableInfo.Index>(2);
        _indicesPayments.add(new TableInfo.Index("index_payments_studentId", false, Arrays.asList("studentId"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_monthKey", false, Arrays.asList("monthKey"), Arrays.asList("ASC")));
        final TableInfo _infoPayments = new TableInfo("payments", _columnsPayments, _foreignKeysPayments, _indicesPayments);
        final TableInfo _existingPayments = TableInfo.read(db, "payments");
        if (!_infoPayments.equals(_existingPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "payments(dev.guruprasath.feeledger.data.local.PaymentEntity).\n"
                  + " Expected:\n" + _infoPayments + "\n"
                  + " Found:\n" + _existingPayments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6d368eeeaa9f215ef3492d81a40e1c58", "a6212bd279043e784988d94e86f9696e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "students","payments");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `students`");
      _db.execSQL("DELETE FROM `payments`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(StudentDao.class, StudentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentDao.class, PaymentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public StudentDao students() {
    if (_studentDao != null) {
      return _studentDao;
    } else {
      synchronized(this) {
        if(_studentDao == null) {
          _studentDao = new StudentDao_Impl(this);
        }
        return _studentDao;
      }
    }
  }

  @Override
  public PaymentDao payments() {
    if (_paymentDao != null) {
      return _paymentDao;
    } else {
      synchronized(this) {
        if(_paymentDao == null) {
          _paymentDao = new PaymentDao_Impl(this);
        }
        return _paymentDao;
      }
    }
  }
}
