package be.lmenten.criminalysis.db;

import be.lmenten.util.jdbc.h2.H2Database;
import be.lmenten.util.jdbc.h2.H2TableHelper;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class Person
	extends CriminalysisObject<Person,Long>
{
	public static final String PERSONS_TABLE_NAME = "persons";

	public static final String PERSONS_COLUMN_ID = PRIMARY_KEY_COLUMN_NAME;
	public static final String PERSONS_COLUMN_FAMILY_NAME = "familyName";
	public static final String PERSONS_COLUMN_GIVEN_NAME = "givenName";
	public static final String PERSONS_COLUMN_GENDER = "gender";
	public static final String PERSONS_COLUMN_EMAIL = "email";
	public static final String PERSONS_COLUMN_PHONE = "phone";

	private static final String SQL_CREATE_TABLE_PERSONS
		=	"CREATE TABLE " + PERSONS_TABLE_NAME + " "
		+	"( "
		+		PERSONS_COLUMN_ID + PRIMARY_KEY_COLUMN_DEF_LONG + ", "
		+		PERSONS_COLUMN_FAMILY_NAME + " VARCHAR, "
		+		PERSONS_COLUMN_GIVEN_NAME + " VARCHAR ARRAY, "
		+       PERSONS_COLUMN_GENDER + " VARCHAR, "
		+		PERSONS_COLUMN_EMAIL + " VARCHAR, "
		+		PERSONS_COLUMN_PHONE + " VARCHAR "
		+	")"
		;

	// ========================================================================
	// = Data =================================================================
	// ========================================================================

	private String familyName;
	private String [] givenNames;
	private String gender;
	private String email;
	private String phone;

	// ========================================================================
	// = Constructors =========================================================
	// ========================================================================

	public Person()
	{
	}

	private Person( ResultSet rs, H2Database database )
		throws SQLException
	{
		familyName = rs.getString( PERSONS_COLUMN_FAMILY_NAME );

		Array array = rs.getArray( PERSONS_COLUMN_GIVEN_NAME );
		Object [] arrayObject = (Object[]) array.getArray();
		givenNames = new String[arrayObject.length];
		for( int i = 0; i < arrayObject.length; i++ )
		{
			givenNames[i] = arrayObject[i].toString();
		}

		gender = rs.getString( PERSONS_COLUMN_GENDER );
		email = rs.getString( PERSONS_COLUMN_EMAIL );
		phone = rs.getString( PERSONS_COLUMN_PHONE );

		setDirty( false );
	}


	// ========================================================================
	// = Getters / Setters ====================================================
	// ========================================================================

	public String getFamilyName()
	{
		return familyName;
	}

	public void setFamilyName( @NotNull String familyName )
	{
		String oldFamilyName = this.familyName;
		if( ! familyName.equals( oldFamilyName ) )
		{
			this.familyName = familyName;

//			propertyChangeSupport.firePropertyChange( PERSONS_COLUMN_FAMILY_NAME, oldFamilyName, familyName );
			setDirty( true );
		}
	}

	public String [] getGivenNames()
	{
		return givenNames;
	}

	public void setGivenNames( @NotNull String ... givenNames )
	{
		String [] oldGivenName = this.givenNames;
		if( ! givenNames.equals( oldGivenName ) )
		{
			this.givenNames = givenNames;

//			propertyChangeSupport.firePropertyChange( PERSONS_COLUMN_GIVEN_NAME, oldGivenName, givenName );
			setDirty( true );
		}
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender( @NotNull String gender )
	{
		String oldGender = this.gender;
		if( ! gender.equals( oldGender ) )
		{
			this.gender = gender;

//			propertyChangeSupport.firePropertyChange( PERSONS_COLUMN_GENDER, oldGender, gender );
		}
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( @NotNull String email )
	{
		String oldEmail = this.email;
		if( ! email.equals( oldEmail ) )
		{
			this.email = email;

//			propertyChangeSupport.firePropertyChange( PERSONS_COLUMN_EMAIL, oldEmail, email );
			setDirty( true );
		}
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone( @NotNull String phone )
	{
		String oldPhone = this.phone;
		if( ! phone.equals( oldPhone ) )
		{
			this.phone = phone;

//			propertyChangeSupport.firePropertyChange( PERSONS_COLUMN_PHONE, oldPhone, phone );
			setDirty( true );
		}
	}

	// ========================================================================
	// = H2TableHelper ========================================================
	// ========================================================================

	public static final H2TableHelper<Person,Long> helper
		= new H2TableHelper<>()
	{
		@Override
		public String getTableName()
		{
			return PERSONS_TABLE_NAME;
		}

		@Override
		public Class<Person> getTableClass()
		{
			return Person.class;
		}

		@Override
		public Class<Long> getPrimaryKeyType()
		{
			return Long.class;
		}

		// --------------------------------------------------------------------

		@Override
		public Person createInstance( ResultSet rs, H2Database database )
			throws SQLException
		{
			return new Person( rs, database );
		}

		@Override
		public void save( H2Database database, Person instance, boolean update )
			throws SQLException
		{
			if( update )
			{
				final String SQL_UPDATE_PLAYER
					=   "UPDATE " + PERSONS_TABLE_NAME + " "
					+   "SET " + PERSONS_COLUMN_FAMILY_NAME + " = ?, "
					+               PERSONS_COLUMN_GIVEN_NAME + " = ?, "
					+	            PERSONS_COLUMN_GENDER + " = ?, "
					+               PERSONS_COLUMN_EMAIL + " = ?, "
					+               PERSONS_COLUMN_PHONE + " = ? "
					+   "WHERE " + PERSONS_COLUMN_ID + " = ?"
					;

				try( PreparedStatement stmt = database.prepareStatement( SQL_UPDATE_PLAYER ) )
				{
					stmt.setString( 1, instance.getFamilyName() );
					stmt.setArray( 2, database.getConnection().createArrayOf("VARCHAR", instance.givenNames ) );
					stmt.setString( 3, instance.getGender() );
					stmt.setString( 4, instance.getEmail() );
					stmt.setString( 5, instance.getPhone() );
					stmt.setLong( 6, instance.getId() );
					stmt.executeUpdate();
				}
			}
			else
			{
				final String SQL_INSERT_PLAYER
					=   "INSERT INTO " + PERSONS_TABLE_NAME + " "
					+   "VALUES( DEFAULT, ?, ?, ? , ?, ? )"
					;

				try( PreparedStatement stmt = database.prepareStatement( SQL_INSERT_PLAYER, true ) )
				{
					stmt.setString( 1, instance.getFamilyName() );
					stmt.setArray( 2, database.getConnection().createArrayOf("VARCHAR", instance.givenNames ) );
					stmt.setString( 3, instance.getGender() );
					stmt.setString( 4, instance.getEmail() );
					stmt.setString( 5, instance.getPhone() );
					stmt.executeUpdate();

					try( ResultSet rs = stmt.getGeneratedKeys() )
					{
						rs.next();
						instance.setId( rs.getLong( 1 ) );
					}
				}
			}
		}

		// --------------------------------------------------------------------

		@Override
		public void onCreate( H2Database database )
			throws SQLException
		{
			try( Statement stmt = database.createStatement() )
			{
				stmt.execute( SQL_CREATE_TABLE_PERSONS );
			}
		}
	};
}
