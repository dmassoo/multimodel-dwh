package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.translation.Translator;
import com.google.common.base.Charsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.cypher2sql.PassAvailables;
import org.cytosm.cypher2sql.lowering.exceptions.Cypher2SqlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.dmasso.multidwh.common.enums.DbType.OLAP;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CypherToSqlTranslator implements Translator {
    @Value("${gtop.path}")
    private final String pathToGtop;

    @Override
    public String translate(String query) {
        GTopInterfaceImpl gTopInterface;
        try {
            gTopInterface = getGTopInterface();
            return tryTranslateCypherToSql(gTopInterface, query);
        } catch (IOException e) {
            throw new RuntimeException("No gTop file provided", e);
        }
    }

    private String tryTranslateCypherToSql(GTopInterfaceImpl gTopInterface, String cypherQuery) {
        try {
            return PassAvailables.cypher2sqlOnExpandedPaths(gTopInterface, cypherQuery).toSQLString();
        } catch (Cypher2SqlException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private GTopInterfaceImpl getGTopInterface() throws IOException {
        String jsonInString = FileUtils.readFileToString(new File(getPathToGtop()), Charsets.UTF_8);
        return new RelationalGTopInterface(jsonInString);
    }

    private String getPathToGtop() {
        return pathToGtop;
    }

    @Override
    public DbType getType() {
        return OLAP;
    }
}
