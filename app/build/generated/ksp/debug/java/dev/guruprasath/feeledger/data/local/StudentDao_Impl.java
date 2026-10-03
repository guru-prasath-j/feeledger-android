package dev.guruprasath.feeledger.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StudentDao_Impl implements StudentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StudentEntity> __insertionAdapterOfStudentEntity;

  private final EntityDeletionOrUpdateAdapter<StudentEntity> __updateAdapterOfStudentEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetActive;

  public StudentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStudentEntity = new EntityInsertionAdapter<StudentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `students` (`id`,`name`,`batch`,`guardianPhoneEnc`,`monthlyFeePaise`,`dueDay`,`startMonth`,`active`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StudentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBatch());
        statement.bindString(4, entity.getGuardianPhoneEnc());
        statement.bindLong(5, entity.getMonthlyFeePaise());
        statement.bindLong(6, entity.getDueDay());
        statement.bindLong(7, entity.getStartMonth());
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfStudentEntity = new EntityDeletionOrUpdateAdapter<StudentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `students` SET `id` = ?,`name` = ?,`batch` = ?,`guardianPhoneEnc` = ?,`monthlyFeePaise` = ?,`dueDay` = ?,`startMonth` = ?,`active` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StudentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBatch());
        statement.bindString(4, entity.getGuardianPhoneEnc());
        statement.bindLong(5, entity.getMonthlyFeePaise());
        statement.bindLong(6, entity.getDueDay());
        statement.bindLong(7, entity.getStartMonth());
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfSetActive = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE students SET active = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final StudentEntity student, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfStudentEntity.insertAndReturnId(student);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final StudentEntity student, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfStudentEntity.handle(student);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setActive(final long id, final boolean active,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetActive.acquire();
        int _argIndex = 1;
        final int _tmp = active ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetActive.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StudentEntity>> observeAll() {
    final String _sql = "SELECT * FROM students ORDER BY name COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"students"}, new Callable<List<StudentEntity>>() {
      @Override
      @NonNull
      public List<StudentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBatch = CursorUtil.getColumnIndexOrThrow(_cursor, "batch");
          final int _cursorIndexOfGuardianPhoneEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "guardianPhoneEnc");
          final int _cursorIndexOfMonthlyFeePaise = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyFeePaise");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfStartMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startMonth");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<StudentEntity> _result = new ArrayList<StudentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBatch;
            _tmpBatch = _cursor.getString(_cursorIndexOfBatch);
            final String _tmpGuardianPhoneEnc;
            _tmpGuardianPhoneEnc = _cursor.getString(_cursorIndexOfGuardianPhoneEnc);
            final long _tmpMonthlyFeePaise;
            _tmpMonthlyFeePaise = _cursor.getLong(_cursorIndexOfMonthlyFeePaise);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final int _tmpStartMonth;
            _tmpStartMonth = _cursor.getInt(_cursorIndexOfStartMonth);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new StudentEntity(_tmpId,_tmpName,_tmpBatch,_tmpGuardianPhoneEnc,_tmpMonthlyFeePaise,_tmpDueDay,_tmpStartMonth,_tmpActive,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<StudentEntity> observe(final long id) {
    final String _sql = "SELECT * FROM students WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"students"}, new Callable<StudentEntity>() {
      @Override
      @Nullable
      public StudentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBatch = CursorUtil.getColumnIndexOrThrow(_cursor, "batch");
          final int _cursorIndexOfGuardianPhoneEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "guardianPhoneEnc");
          final int _cursorIndexOfMonthlyFeePaise = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyFeePaise");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfStartMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startMonth");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final StudentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBatch;
            _tmpBatch = _cursor.getString(_cursorIndexOfBatch);
            final String _tmpGuardianPhoneEnc;
            _tmpGuardianPhoneEnc = _cursor.getString(_cursorIndexOfGuardianPhoneEnc);
            final long _tmpMonthlyFeePaise;
            _tmpMonthlyFeePaise = _cursor.getLong(_cursorIndexOfMonthlyFeePaise);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final int _tmpStartMonth;
            _tmpStartMonth = _cursor.getInt(_cursorIndexOfStartMonth);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new StudentEntity(_tmpId,_tmpName,_tmpBatch,_tmpGuardianPhoneEnc,_tmpMonthlyFeePaise,_tmpDueDay,_tmpStartMonth,_tmpActive,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object all(final Continuation<? super List<StudentEntity>> $completion) {
    final String _sql = "SELECT * FROM students";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StudentEntity>>() {
      @Override
      @NonNull
      public List<StudentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBatch = CursorUtil.getColumnIndexOrThrow(_cursor, "batch");
          final int _cursorIndexOfGuardianPhoneEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "guardianPhoneEnc");
          final int _cursorIndexOfMonthlyFeePaise = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyFeePaise");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfStartMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startMonth");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<StudentEntity> _result = new ArrayList<StudentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBatch;
            _tmpBatch = _cursor.getString(_cursorIndexOfBatch);
            final String _tmpGuardianPhoneEnc;
            _tmpGuardianPhoneEnc = _cursor.getString(_cursorIndexOfGuardianPhoneEnc);
            final long _tmpMonthlyFeePaise;
            _tmpMonthlyFeePaise = _cursor.getLong(_cursorIndexOfMonthlyFeePaise);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final int _tmpStartMonth;
            _tmpStartMonth = _cursor.getInt(_cursorIndexOfStartMonth);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new StudentEntity(_tmpId,_tmpName,_tmpBatch,_tmpGuardianPhoneEnc,_tmpMonthlyFeePaise,_tmpDueDay,_tmpStartMonth,_tmpActive,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final long id, final Continuation<? super StudentEntity> $completion) {
    final String _sql = "SELECT * FROM students WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StudentEntity>() {
      @Override
      @Nullable
      public StudentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBatch = CursorUtil.getColumnIndexOrThrow(_cursor, "batch");
          final int _cursorIndexOfGuardianPhoneEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "guardianPhoneEnc");
          final int _cursorIndexOfMonthlyFeePaise = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyFeePaise");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfStartMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startMonth");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final StudentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBatch;
            _tmpBatch = _cursor.getString(_cursorIndexOfBatch);
            final String _tmpGuardianPhoneEnc;
            _tmpGuardianPhoneEnc = _cursor.getString(_cursorIndexOfGuardianPhoneEnc);
            final long _tmpMonthlyFeePaise;
            _tmpMonthlyFeePaise = _cursor.getLong(_cursorIndexOfMonthlyFeePaise);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final int _tmpStartMonth;
            _tmpStartMonth = _cursor.getInt(_cursorIndexOfStartMonth);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new StudentEntity(_tmpId,_tmpName,_tmpBatch,_tmpGuardianPhoneEnc,_tmpMonthlyFeePaise,_tmpDueDay,_tmpStartMonth,_tmpActive,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
