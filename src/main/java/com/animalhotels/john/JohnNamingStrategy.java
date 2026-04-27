package com.animalhotels.john;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class JohnNamingStrategy extends PhysicalNamingStrategySnakeCaseImpl {
    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment context) {
        Identifier name = super.toPhysicalTableName(logicalName, context);

        return new Identifier("john_" + name.getText(), name.isQuoted());
    }
}
