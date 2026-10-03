package dev.guruprasath.feeledger.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
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
public final class PaymentDao_Impl implements PaymentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PaymentEntity> __insertionAdapterOfPaymentEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public PaymentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPaymentEntity = new EntityInsertionAdapter<PaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `payments` (`id`,`studentId`,`monthKey`,`amountPaise`,`method`,`utrEnc`,`paidAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStudentId());
        statement.bindLong(3, entity.getMonthKey());
        statement.bindLong(4, entity.getAmountPaise());
        statement.bindString(5, entity.getMethod());
        if (entity.getUtrEnc() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getUtrEnc());
        }
        statement.bindLong(7, entity.getPaidAt());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payments WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PaymentEntity payment, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPaymentEntity.insertAndReturnId(payment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PaymentEntity>> observeAll() {
    final String _sql = "SELECT * FROM payments ORDER BY paidAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payments"}, new Callable<List<PaymentEntity>>() {
      @Override
      @NonNull
      public List<PaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfAmountPaise = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaise");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfUtrEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "utrEnc");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final List<PaymentEntity> _result = new ArrayList<PaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStudentId;
            _tmpStudentId = _cursor.getLong(_cursorIndexOfStudentId);
            final int _tmpMonthKey;
            _tmpMonthKey = _cursor.getInt(_cursorIndexOfMonthKey);
            final long _tmpAmountPaise;
            _tmpAmountPaise = _cursor.getLong(_cursorIndexOfAmountPaise);
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpUtrEnc;
            if (_cursor.isNull(_cursorIndexOfUtrEnc)) {
              _tmpUtrEnc = null;
            } else {
              _tmpUtrEnc = _cursor.getString(_cursorIndexOfUtrEnc);
            }
            final long _tmpPaidAt;
            _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
            _item = new PaymentEntity(_tmpId,_tmpStudentId,_tmpMonthKey,_tmpAmountPaise,_tmpMethod,_tmpUtrEnc,_tmpPaidAt);
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
  public Flow<List<PaymentEntity>> observeForStudent(final long studentId) {
    final String _sql = "SELECT * FROM payments WHERE studentId = ? ORDER BY monthKey DESC, paidAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, studentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payments"}, new Callable<List<PaymentEntity>>() {
      @Override
      @NonNull
      public List<PaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfAmountPaise = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaise");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfUtrEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "utrEnc");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final List<PaymentEntity> _result = new ArrayList<PaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStudentId;
            _tmpStudentId = _cursor.getLong(_cursorIndexOfStudentId);
            final int _tmpMonthKey;
            _tmpMonthKey = _cursor.getInt(_cursorIndexOfMonthKey);
            final long _tmpAmountPaise;
            _tmpAmountPaise = _cursor.getLong(_cursorIndexOfAmountPaise);
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpUtrEnc;
            if (_cursor.isNull(_cursorIndexOfUtrEnc)) {
              _tmpUtrEnc = null;
            } else {
              _tmpUtrEnc = _cursor.getString(_cursorIndexOfUtrEnc);
            }
            final long _tmpPaidAt;
            _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
            _item = new PaymentEntity(_tmpId,_tmpStudentId,_tmpMonthKey,_tmpAmountPaise,_tmpMethod,_tmpUtrEnc,_tmpPaidAt);
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
  public Object all(final Continuation<? super List<PaymentEntity>> $completion) {
    final String _sql = "SELECT * FROM payments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PaymentEntity>>() {
      @Override
      @NonNull
      public List<PaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfAmountPaise = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaise");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfUtrEnc = CursorUtil.getColumnIndexOrThrow(_cursor, "utrEnc");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final List<PaymentEntity> _result = new ArrayList<PaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStudentId;
            _tmpStudentId = _cursor.getLong(_cursorIndexOfStudentId);
            final int _tmpMonthKey;
            _tmpMonthKey = _cursor.getInt(_cursorIndexOfMonthKey);
            final long _tmpAmountPaise;
            _tmpAmountPaise = _cursor.getLong(_cursorIndexOfAmountPaise);
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpUtrEnc;
            if (_cursor.isNull(_cursorIndexOfUtrEnc)) {
              _tmpUtrEnc = null;
            } else {
              _tmpUtrEnc = _cursor.getString(_cursorIndexOfUtrEnc);
            }
            final long _tmpPaidAt;
            _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
            _item = new PaymentEntity(_tmpId,_tmpStudentId,_tmpMonthKey,_tmpAmountPaise,_tmpMethod,_tmpUtrEnc,_tmpPaidAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
